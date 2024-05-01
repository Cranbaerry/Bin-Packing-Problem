//package algorithms;
//
//import factories.ItemFactory;
//import objects.Problem;
//import objects.Solution;
//import java.util.*;
//
//public class GeneticAlgorithmV2 implements Algorithm {
//    private int populationSize;
//    private double mutationRate;
//    private double crossoverRate;
//    private int elitismCount;
//    private int binCapacity;
//    private List<Solution> population;
//    private Random random;
//
//    private Problem problem;
//
//    public GeneticAlgorithmV2() {
//        // Initialize custom parameters here
//        this.populationSize = 100;
//        this.mutationRate = 0.01;
//        this.crossoverRate = 0.8;
//        this.elitismCount = 3;
//
//    }
//
//    @Override
//    public Solution solve(Problem problem) {
//        this.random = new Random();
//        this.problem = problem;
//        this.binCapacity = problem.getCapacity();
//        Solution initialSolution = randomizeSolution(problem.items.flatten(), problem);
//
//        // Initialize population
//        population = new ArrayList<>();
//        for (int i = 0; i < populationSize; i++) {
//            population.add(randomizeSolution(problem.items.flatten(), problem));
//        }
//
//        Solution fittestSolution = findFittestSolution();
//        int generationsWithoutImprovement = 0;
//
//        while (generationsWithoutImprovement < 30) {
//            Collections.sort(population, Comparator.comparingDouble(Solution::getFitness));
//            List<Solution> newPopulation = evolvePopulation();
//            Solution newFittest = findFittestSolution();
//
//            if (newFittest.getFitness() <= fittestSolution.getFitness()) {
//                generationsWithoutImprovement++;
//            } else {
//                fittestSolution = newFittest;
//                generationsWithoutImprovement = 0;
//            }
//
//            population = newPopulation;
//        }
//
//        return fittestSolution;
//    }
//
//    private Solution randomizeSolution(List<Integer> items, Problem problem) {
//        // Randomize initial solution similarly to your TabuSearchAlgorithm
//        Solution solution = new Solution(problem);
//        ItemFactory bin = new ItemFactory();
//        for (int item : items) {
//            if (random.nextBoolean() || bin.getTotalWeight() + item > this.binCapacity) {
//                solution.bins.createBin(bin);
//                bin = new ItemFactory();
//            }
//            bin.addItem(item, 1);
//        }
//        solution.bins.createBin(bin); // Add the last bin
//        return solution;
//    }
//
//    private Solution findFittestSolution() {
//        return Collections.max(population, Comparator.comparingDouble(Solution::getFitness));
//    }
//
//    private List<Solution> evolvePopulation() {
//        List<Solution> newPopulation = new ArrayList<>();
//
//        // Implement elitism
//        for (int i = 0; i < elitismCount; i++) {
//            newPopulation.add(population.get(i));
//        }
//
//        // Implement crossover
//        while (newPopulation.size() < populationSize) {
//            Solution parent1 = selectParent();
//            Solution parent2 = selectParent();
//            if (random.nextDouble() < crossoverRate) {
//                newPopulation.add(crossover(parent1, parent2));
//            } else {
//                newPopulation.add(parent1);
//            }
//        }
//
//        // Implement mutation
//        newPopulation.forEach(this::mutate);
//
//        return newPopulation;
//    }
//
//    private Solution crossover(Solution parent1, Solution parent2) {
//        // Combine bins from both parents to produce a new child Solution
//        Solution child = new Solution(problem);
//
//        List<ItemFactory> parent1Bins = parent1.bins.getBins();
//        List<ItemFactory> parent2Bins = parent2.bins.getBins();
//
//        // Choose random bin from parent 1 and parent 2
//        int parent1BinIndex = random.nextInt(parent1Bins.size());
//        int parent2BinIndex = random.nextInt(parent2Bins.size());
//
//        // Get the bins from the parents
//        ItemFactory parent1Bin = parent1Bins.get(parent1BinIndex);
//        ItemFactory parent2Bin = parent2Bins.get(parent2BinIndex);
//
//        // Choose crossover point
//        int crossoverPoint1 = random.nextInt(parent1Bin.getNumberOfItems());
//        int crossoverPoint2 = random.nextInt(parent2Bin.getNumberOfItems());
//
//        //
//
//        // Add bins from parent1 to the child until the crossover point
//        for (int i = 0; i < crossoverPoint1; i++) {
//            new ItemFactory(parent1Bins.get(i)));
//        }
//        //loop through parent1Bins
//        for (int i = 0; i < parent1Bins.size(); i++) {
//            child.bins.createBin(new ItemFactory(parent1Bins.get(i)));
//        }
//        child.bins.createBin();
//
//        // Similarly, add bins from parent2 to the child from the crossover point
//        for (int i = crossoverPoint2; i < parent2Bins.size(); i++) {
//            child.bins.createBin(new ItemFactory(parent2Bins.get(i)));
//        }
//
//        // After the crossover, some items may be over-represented in the child,
//        // and some may be under-represented. A repair function is needed to
//        // adjust these quantities to maintain feasibility of the child solution.
//        // Below is the start of a repair function:
//
//        repairSolution(child);
//
//        return child;
//    }
//
//    private void repairSolution(Solution solution) {
//        // Calculate the total quantity of each item in the initial problem
//        HashMap<Integer, Integer> totalItemQuantities = new HashMap<>();
//        for (int item : problem.items.flatten()) {
//            totalItemQuantities.put(item, totalItemQuantities.getOrDefault(item, 0) + 1);
//        }
//
//        // Calculate the total quantity of each item in the child solution
//        HashMap<Integer, Integer> childItemQuantities = new HashMap<>();
//        for (ItemFactory bin : solution.bins.getBins()) {
//            for (int item : bin.getItems().keySet()) {
//                childItemQuantities.put(item, childItemQuantities.getOrDefault(item, 0) + bin.getQuantity(item));
//            }
//        }
//
//        // Adjust child solution to correct quantities
//        for (int item : totalItemQuantities.keySet()) {
//            int quantityDifference = totalItemQuantities.get(item) - childItemQuantities.getOrDefault(item, 0);
//            while (quantityDifference != 0) {
//                if (quantityDifference > 0) {
//                    // Add missing items to bins
//                    addItemToBin(solution, item);
//                    quantityDifference--;
//                } else {
//                    // Remove excess items from bins
//                    removeItemFromBin(solution, item);
//                    quantityDifference++;
//                }
//            }
//        }
//    }
//
//    private void addItemToBin(Solution solution, int item) {
//        // Try to fit the item into an existing bin
//        for (ItemFactory bin : solution.bins.getBins()) {
//            if (bin.getTotalWeight() + item <= binCapacity) {
//                bin.addItem(item, 1);
//                return;
//            }
//        }
//        // If no existing bin can accommodate, create a new bin
//        ItemFactory newBin = new ItemFactory();
//        newBin.addItem(item, 1);
//        solution.bins.createBin(newBin);
//    }
//
//    private void removeItemFromBin(Solution solution, int item) {
//        // Find a bin that contains the item and remove it
//        for (ItemFactory bin : solution.bins.getBins()) {
//            if (bin.containsItem(item) && bin.getQuantity(item) > 0) {
//                bin.removeItem(item, 1);
//                // Optionally, if bin is now empty, remove it from the solution
//                if (bin.getTotalWeight() == 0) {
//                    solution.bins.getBins().remove(bin);
//                }
//                return;
//            }
//        }
//    }
//
////    private Solution crossover(Solution parent1, Solution parent2) {
////        // Combine bins from both parents to produce a new child Solution
////        Solution child = new Solution(problem);
////        List<ItemFactory> parent1Bins = new ArrayList<>(parent1.bins.getBins());
////        List<ItemFactory> parent2Bins = new ArrayList<>(parent2.bins.getBins());
////
////        int crossoverPoint = random.nextInt(parent1Bins.size());
////
////        for (int i = 0; i < crossoverPoint; i++) {
////            child.bins.createBin(new ItemFactory(parent1Bins.get(i)));
////        }
////
////        for (int i = crossoverPoint; i < parent2Bins.size(); i++) {
////            child.bins.createBin(new ItemFactory(parent2Bins.get(i)));
////        }
////
////        // Here, you should be adding logic to handle the case where child bins exceed
////        // the bin capacity or an item is in more than one bin. One solution is to
////        // reassign all items in a first fit fashion after crossover.
////
////        return child;
////    }
//
//    private void mutate(Solution solution) {
//        // Randomly mutate a solution by moving an item between bins
//        List<ItemFactory> bins = solution.bins.getBins();
//        if (bins.isEmpty()) return;
//
//        int binIndex = random.nextInt(bins.size());
//        ItemFactory selectedBin = bins.get(binIndex);
//        List<Integer> items = selectedBin.flatten();
//
//        if (items.isEmpty()) return;
//
//        int itemIndex = random.nextInt(items.size());
//        int item = items.get(itemIndex);
//
//        // Remove the item from the original bin
//        selectedBin.removeItem(item, 1);
//
//        // Add the item to a random different bin
//        int newBinIndex = random.nextInt(bins.size());
//        while (binIndex == newBinIndex) {
//            newBinIndex = random.nextInt(bins.size());
//        }
//        bins.get(newBinIndex).addItem(item, 1);
//
//        // This mutation does not guarantee that the bin capacity constraint
//        // will be satisfied. We should have additional logic to handle this.
//    }
//
//    private Solution selectParent() {
//        // Select a parent solution using tournament selection
//        int tournamentSize = 5; // Tune this parameter as needed
//        Solution best = null;
//        double bestFitness = Double.MIN_VALUE;
//
//        for (int i = 0; i < tournamentSize; i++) {
//            int randomIndex = random.nextInt(population.size());
//            Solution selected = population.get(randomIndex);
//            double fitness = selected.getFitness();
//            if (best == null || fitness > bestFitness) { // Looking for higher fitness because higher is better
//                best = selected;
//                bestFitness = fitness;
//            }
//        }
//
//        return best;
//    }
//}