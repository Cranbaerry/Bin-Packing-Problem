//        Tabu Search (TS):
//        determine initial candidate solution s
//        While termination criterion is not satisfied:
//        determine set N' of non-tabu neighbours of s
//        choose a best improving candidate solution s' in N'
//        update tabu attributes based on s'
//        S:= s'

package algorithms;

import factories.ItemFactory;
import objects.Problem;
import objects.Solution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class TabuSearchAlgorithm implements Algorithm {
    private int tabuTenure; // Tabu tenure parameter
    private int convergenceIterations; // Number of iterations without improvement before stopping
    private int currentIteration; // Current iteration count
    private int binSizeThreshold; // Threshold for bin size difference
    private List<Solution> tabuList; // Tabu list to store visited solutions
    private int binCapacity;
    private List<Integer> items;
    private int maximumNeighbors;

    public TabuSearchAlgorithm() {
        this.convergenceIterations = 50;
        this.maximumNeighbors = 50;
        this.binSizeThreshold = 10;
    }

    @Override
    public Solution solve(Problem problem) {
        long startTime = System.currentTimeMillis();
        this.currentIteration = 0;
        this.binCapacity = problem.getCapacity();
        this.items = problem.items.flatten();
        this.tabuList = new ArrayList<>();
        this.tabuTenure = 10;

        // Generate initial solution using first fit algorithm
        Solution currentSolution = firstFitSolution(this.items, problem);
        //Solution currentSolution = randomizeSolution(this.items, problem);
        int unchangedIterations = 0;
        try {
            Solution bestSolution = (Solution) currentSolution.clone();
            //hasmap to store the number of bins for each iteration
            HashMap<Integer, Integer> iterationData = new HashMap<>();
            do {
                List<Solution> neighbors = generateNeighbors(currentSolution, problem); // Generate neighbors
                Solution bestNeighbor = getBestNeighbor(neighbors); // Find the best improving neighbor
                if (bestNeighbor.getFitness() > bestSolution.getFitness()) {
                    //System.out.println("Iteration " + currentIteration + " Best Neighbor: " + bestNeighbor.getFitness() + " Best Solution: " + bestSolution.getFitness());
                    bestSolution = (Solution) bestNeighbor.clone(); // Update best solution
                    unchangedIterations = 0;
                    tabuTenure = Math.max(1, (int) (tabuTenure * 0.9)); // Decrease tenure on improvement
                } else {
                    unchangedIterations++;
                    tabuTenure = (int) (tabuTenure * 1.1) + 1; // Increase tenure on stagnation
                }

                updateTabuList(bestSolution); // Update tabu attributes based on best solution
                currentSolution = (Solution) bestSolution.clone(); // Move to the best neighbor
                currentIteration++; // Increment iteration count
                iterationData.put(currentIteration, currentSolution.bins.getNumberOfBins());
            } while (unchangedIterations < convergenceIterations);

            currentSolution.setStartTime(startTime);
            return currentSolution.finalizeResult(iterationData);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Solution randomizeSolution(List<Integer> items, Problem problem) {
        Solution solution = new Solution(problem);
        ItemFactory bin = new ItemFactory();
        Random random = new Random();
        for (int item : items) {
            if (random.nextBoolean() || bin.getTotalWeight() + item > this.binCapacity) {
                solution.bins.createBin(bin);
                bin = new ItemFactory();
            }
            bin.addItem(item, 1);
        }

        return solution;
    }

    private Solution firstFitSolution(List<Integer> items, Problem problem) {
        Solution solution = new Solution(problem);
        ItemFactory bin = new ItemFactory();
        for (int item : items) {
            if (bin.getTotalWeight() + item > this.binCapacity) {
                solution.bins.createBin(bin);
                bin = new ItemFactory();
            }
            bin.addItem(item, 1);
        }

        return solution;
    }

    private List<Solution> generateNeighbors(Solution solution, Problem problem) throws CloneNotSupportedException {
        List<Solution> neighbors = new ArrayList<>();
        ArrayList<ItemFactory> bins = solution.bins.getBins();
        int neighborCount = 0;
        while (neighborCount < maximumNeighbors) {
            // Pick a random bin
            Random random = new Random();
            int binIndex = random.nextInt(bins.size());
            int otherBinIndex = random.nextInt(bins.size());
            while (otherBinIndex == binIndex) {
                otherBinIndex = random.nextInt(bins.size());
            }

            ItemFactory bin = bins.get(binIndex);
            ItemFactory otherBin = bins.get(otherBinIndex);
            List<Integer> items = bin.flatten();
            List<Integer> otherItems = otherBin.flatten();

            if (items.isEmpty() || otherItems.isEmpty()) {
                // System.out.println("Empty bin, skipping");
                continue;
            }

            // If the total weight of the items in the two bins is not significantly different
            // (e.g., more than a certain threshold),
            // continue to the next iteration without swapping.
            if (Math.abs(bin.getTotalWeight() - otherBin.getTotalWeight()) < binSizeThreshold && new Random().nextBoolean()) {
                // System.out.printf("Abs weight difference: %d, Threshold: %d%n", Math.abs(bin.getTotalWeight() - otherBin.getTotalWeight()),binSizeThreshold);
                continue;
            }

            // Pick a random item from the bin
            int itemIndex = random.nextInt(items.size());
            int otherItemIndex = random.nextInt(otherItems.size());

            // Swap the items between the bins
            int item = items.get(itemIndex);
            int otherItem = otherItems.get(otherItemIndex);

            if (item == otherItem) {
                continue;
            }

            Solution neighbor = new Solution(problem);
            neighbor.bins = solution.bins.clone();
            ArrayList<ItemFactory> neighborBins = neighbor.bins.getBins();
            bin = neighborBins.get(binIndex);
            otherBin = neighborBins.get(otherBinIndex);

            switch (random.nextInt(2)) {
                case 0:
                    // Swap the items between the bins
                    bin.removeItem(item, 1);
                    bin.addItem(otherItem, 1);
                    otherBin.removeItem(otherItem, 1);
                    otherBin.addItem(item, 1);
                    break;
                case 1:
                    // Move a random item from one bin to another
                    item = items.get(random.nextInt(items.size()));
                    bin.removeItem(item, 1);
                    otherBin.addItem(item, 1);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value");
            }

            if (bin.getTotalWeight() > binCapacity || otherBin.getTotalWeight() > binCapacity) {
                // System.out.println("Over capacity, skipping");
                continue;
            }
            if (bin.getTotalWeight() <= 0) {
                neighborBins.remove(bin);
            }
            if (otherBin.getTotalWeight() <= 0) {
                neighborBins.remove(otherBin);
            }

            neighbors.add(neighbor);
            neighborCount++;
        }

        return neighbors;
    }

    // Find the best improving neighbor among a list of neighbors
    private Solution getBestNeighbor(List<Solution> neighbors) {
        Solution bestNeighbor = null;
        double bestFitness = Double.MIN_VALUE; // Start with the worst fitness
        for (Solution neighbor : neighbors) {
            double fitness = neighbor.getFitness();
            //System.out.println("Fitness: " + fitness);
            if (fitness > bestFitness && !tabuList.contains(neighbor)) { // Look for a neighbor with a higher fitness
                bestFitness = fitness;
                bestNeighbor = neighbor;
            }
        }
        return bestNeighbor;
    }

    // Update tabu attributes based on the current solution
    private void updateTabuList(Solution solution) {
        if (tabuList.contains(solution)) return;
        tabuList.add(solution);
        if (tabuList.size() > tabuTenure) {
            tabuList.remove(0);
        }
    }
}
