package algorithms;

import factories.ItemFactory;
import objects.Problem;
import objects.Solution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class FireFlyAlgorithm implements Algorithm {
    private int fireflyCount;
    private double attractivenessBase;
    private double alpha;
    private double beta;
    private double gamma;
    private List<Integer> items;
    private int binCapacity;

    public FireFlyAlgorithm() {
        // Initializing custom parameters
        this.fireflyCount = 100;
        this.attractivenessBase = 0.1;
        this.alpha = 0.5;
        this.beta = 1.0;
        this.gamma = 0.1;
        items = new ArrayList<Integer>();
    }

    public Solution solve(Problem problem, int timeLimit) {
        Solution solution = new Solution(problem);
        this.binCapacity = problem.getCapacity();
        
        while (solution.getCurrentRuntime() < timeLimit * 1000L){
            HashMap<Integer, Integer> problemItems = problem.items.getItems();
            for (int weight : problemItems.keySet()) { // convert hashmap into array of integers
            	for(int i = 0; i < problemItems.get(weight);i++) {
            		items.add(weight);
            	}
            }
            
            int chromosomeLength = items.size();
            List<Firefly> population = new ArrayList<>();
            
            for (int i = 0; i < fireflyCount; i++) {
                population.add(new Firefly(createFireFlies(items, chromosomeLength),binCapacity));}

            for (int iteration = 0; iteration < 100; iteration++) {
                Collections.sort(population, Comparator.comparing(Firefly::getFitness));
                moveFireflies(population);
            }

            // Convert Solution to list of bins
            Firefly bestFirefly = Collections.min(population, Comparator.comparing(Firefly::getFitness));
            ItemFactory binItems = new ItemFactory();
            int currentBinCapacity = 0;
            for (int firefly : bestFirefly.getPosition()) {
                if (currentBinCapacity + firefly > binCapacity) {
                    solution.bins.createBin(binItems);
                    binItems = new ItemFactory();
                    currentBinCapacity = 0;
                }
                binItems.addItem(firefly, 1);
                currentBinCapacity += firefly;
            }
            solution.bins.createBin(binItems);

            break; // Only running one iteration for demonstration, you can remove this break to run for full timeLimit
        }

        return solution.finalizeResult();
    }
    
    private List<Integer> createFireFlies(List<Integer> items, int Particle_Position) {
        List<Integer> individual = new ArrayList<>(items);
        Collections.shuffle(individual);
        return individual;
    }

    private void moveFireflies(List<Firefly> fireflies) {
        Random random = new Random();
        for (Firefly current : fireflies) {
            for (Firefly other : fireflies) {
                if (current.getFitness() < other.getFitness()) {
                    double distance = calculateEuclideanDistance(current.getPosition(), other.getPosition());
                    double betaAttractiveness = beta * Math.exp(-gamma * Math.pow(distance, 2));
                    double moveProb = alpha * (random.nextDouble() - 0.5);

                    for (int i = 0; i < current.getPosition().size(); i++) {
                        if (random.nextDouble() < betaAttractiveness) {
                            int newPosition = current.getPosition().get(i) + (int) moveProb;
                            current.getPosition().set(i, newPosition);
                        }
                    }
                    current.calculateFitness(binCapacity);
                }
            }
        }
    }

    private double calculateEuclideanDistance(List<Integer> position1, List<Integer> position2) {
        double distance = 0;
        for (int i = 0; i < position1.size(); i++) {
            distance += Math.pow(position1.get(i) - position2.get(i), 2);
        }
        return Math.sqrt(distance);
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
    }
}