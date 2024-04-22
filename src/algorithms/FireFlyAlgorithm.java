package algorithms;

import factories.ItemFactory;
import objects.Problem;
import objects.Solution;

import java.util.*;

public class FireFlyAlgorithm implements Algorithm {
    private int populationSize;
    private double attractivenessBase;
    private double alpha;
    private double beta;
    private double gamma;
    private List<Integer> items;
    private int binCapacity;

    public FireFlyAlgorithm() {
        // Initializing custom parameters
        this.populationSize = 100;
        this.attractivenessBase = 0.1;
        this.alpha = 0.5;
        this.beta = 1.0;
        this.gamma = 0.1;
        items = new ArrayList<Integer>();
    }

    public Solution solve(Problem problem) {
        Solution solution = new Solution(problem);
        this.binCapacity = problem.getCapacity();
        this.items = problem.items.flatten();
        int fireFlyPosition = items.size();
        List<Firefly> population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            population.add(new Firefly(createFireFlies(items, fireFlyPosition), binCapacity));
        }

        int iteration = 0;
        int counter = 0;
        Firefly fittestFirefly = Collections.min(population, Comparator.comparing(Firefly::getFitness));
        while (counter < 30) {
            Collections.sort(population, (c1, c2) -> Integer.compare(c1.getFitness(), c2.getFitness()));
            population = moveFireflies(population);
            iteration++;

            Firefly temp = Collections.min(population, Comparator.comparing(Firefly::getFitness));

            if (temp == fittestFirefly) {
                counter++;
            } else {
                fittestFirefly = temp;
                counter = 0;
            }
        }
        // System.out.println("Iteration : " + iteration);

        fittestFirefly = Collections.min(population, Comparator.comparing(Firefly::getFitness));
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


        return solution.finalizeResult();
    }

    private List<Integer> createFireFlies(List<Integer> items, int Particle_Position) {
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

    private List<Firefly> moveFireflies(List<Firefly> fireflies) {
        Random random = new Random();
        List<Firefly> nextPosition = new ArrayList<>();

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
        return fireflies;
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
