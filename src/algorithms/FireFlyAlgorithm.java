package algorithms;

import factories.ItemFactory;
import objects.Problem;
import objects.Solution;

import java.util.*;

public class FireFlyAlgorithm implements Algorithm {
    private int populationSize;
    private double alpha;
    private double beta;
    private double gamma;
    private List<Integer> items;
    private int binCapacity;

    public FireFlyAlgorithm() {
        // Initializing custom parameters
        this.populationSize = 100;
        this.alpha = 0.5;
        this.beta = 1.0;
        this.gamma = 0.5;
    }

    public Solution solve(Problem problem) {
        Solution solution = new Solution(problem);
        this.binCapacity = problem.getCapacity();
        this.items = problem.items.flatten();

        // Initialize population of fireflies
        List<Firefly> population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            population.add(new Firefly(createFireFlies(items), binCapacity));
        }

        int iteration = 0;
        int counter = 0;
        HashMap<Integer, Integer> iterationData = new HashMap<>();
        Firefly fittestFirefly = Collections.min(population, Comparator.comparing(Firefly::getFitness));
        int Fitness_fittestFirefly = fittestFirefly.getFitness();
        List<Integer> Best_fireflyposition = new ArrayList<>(fittestFirefly.getPosition());
        while (counter < 30) {
            population = moveFireflies(population);
            iteration++;
            Firefly temp = Collections.min(population, Comparator.comparing(Firefly::getFitness));
            if (Fitness_fittestFirefly <= temp.getFitness()){
                counter++;
            } else {
                fittestFirefly = temp;
                Best_fireflyposition = new ArrayList<>(fittestFirefly.getPosition());
                Fitness_fittestFirefly = fittestFirefly.getFitness();
                counter = 0;
            }
            iterationData.put(iteration, Fitness_fittestFirefly);
        }
        for (int i = 0; i < fittestFirefly.getPosition().size(); i++) {
            fittestFirefly.getPosition().set(i, Best_fireflyposition.get(i));}
        Collections.shuffle(fittestFirefly.getPosition());
        int currentBinCapacity = 0;
        ItemFactory items = new ItemFactory();
        for (int firefly : fittestFirefly.getPosition()) {
            if (currentBinCapacity + firefly > binCapacity) {// bin full
                solution.bins.createBin(items); //add to list of bins
                items = new ItemFactory(); //create new bin
                currentBinCapacity = 0;
            }
            items.addItem(firefly, 1); //add item to bin
            currentBinCapacity += firefly;
        }

        solution.bins.createBin(items);
        return solution.finalizeResult(iterationData);
    }

    private List<Integer> createFireFlies(List<Integer> items) {
        List<Integer> individual = new ArrayList<>(items);
        Collections.shuffle(individual);
        return individual;
    }

    private double calculateEuclideanDistance(List<Integer> position1, List<Integer> position2) {
        double distance = 0;
        for (int i = 0; i < position1.size(); i++) {
            distance += Math.pow(position1.get(i) - position2.get(i), 2);
        }
        return Math.sqrt(distance);
    }

    private double[] minMaxNormalizeDistance(List<Firefly> fireflies) {
        double min_Distance = Double.MAX_VALUE;
        double max_Distance = Double.MIN_VALUE;
        for (Firefly current : fireflies) {
            for (Firefly other : fireflies) {
                if (current.getFitness() < other.getFitness()) {
                    double distance = calculateEuclideanDistance(other.getPosition(), current.getPosition());
                    min_Distance = Math.min(min_Distance, distance);
                    max_Distance = Math.max(max_Distance, distance);
                }
            }
        }
        return new double[]{min_Distance, max_Distance};
    }

    private List<Firefly> moveFireflies(List<Firefly> fireflies) {
        List<Firefly> newFireflies = new ArrayList<>();
        Random random = new Random();
        double[] distances = minMaxNormalizeDistance(fireflies);
        double minDistance = distances[0];
        double maxDistance = distances[1];

        // Compare Current Firefly with other Fireflies
        for (Firefly current : fireflies) {
            boolean attracted = false;
            for (Firefly other : fireflies) {
                double distance = calculateEuclideanDistance(other.getPosition(), current.getPosition());
                double normalizedDistance = 5 * (distance - minDistance) / (maxDistance - minDistance); // Normalize distance between 0 and 5, or else value for betaAttractiveness will be 0
                double betaAttractiveness = beta * Math.exp(-gamma * Math.pow(normalizedDistance, 2));
                double moveProb = alpha * (random.nextDouble() - 0.5);

                double currentLightIntensity = (current.getFitness() * Math.exp(-gamma * Math.pow(normalizedDistance, 2)));
                double otherLightIntensity = (other.getFitness() * Math.exp(-gamma * Math.pow(normalizedDistance, 2)));
                // If fitness of current firefly is bigger than other firefly, current firefly becomes attracted to other firefly
                if (currentLightIntensity > otherLightIntensity) {
                    if (Math.abs(betaAttractiveness + moveProb) > random.nextDouble()) {
                        List<Integer> new_positionsO = new ArrayList<>(other.getPosition());
                        for (int i = 0; i < current.getPosition().size(); i++) {
                            current.getPosition().set(i, new_positionsO.get(i));
                        }
                    }
                    attracted = true;
                }
            }
            if (!attracted) {// If not attracted to anyone, shuffle the positions
                List<Integer> new_positionsC = new ArrayList<>(current.getPosition());
                Collections.shuffle(new_positionsC);
                for (int i = 0; i < current.getPosition().size(); i++) {
                    current.getPosition().set(i, new_positionsC.get(i));
                }
            }
            current.calculateFitness(binCapacity);
            newFireflies.add(current);
        }
        return newFireflies;
    }
}

class Firefly {
    private List<Integer> position;
    private int fitness;

    public Firefly(List<Integer> position, int binCapacity) {
        this.position = position;
        calculateFitness(binCapacity);
    }

    public List<Integer> getPosition() {
        return position;
    }

    public int getFitness() {
        return fitness;
    }

    public void calculateFitness(int binCapacity) {
        fitness = 0;
        int currentBinCapacity = 0;
        for (int item : position) {
            if (currentBinCapacity + item > binCapacity) {
                fitness++;
                currentBinCapacity = 0;
            }
            currentBinCapacity += item;
        }
        if (currentBinCapacity > 0) {
            fitness++;
        }
    }
}