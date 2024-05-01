package algorithms;

import factories.ItemFactory;
import objects.Problem;
import objects.Solution;

import java.util.*;

public class GeneticAlgorithm implements Algorithm {
    private int populationSize;
    private double mutationRate;
    private double crossoverRate;
    private int elitismCount;
    private List<Integer> items;
    private int binCapacity;


    public GeneticAlgorithm() {
        // Initialize custom parameters here
        this.populationSize = 100;
        this.mutationRate = 0.01;
        this.crossoverRate = 0.8;
        this.elitismCount = 3;
    }

    @Override
    public Solution solve(Problem problem) {
        Solution solution = new Solution(problem); // This should be at the top, it initializes the runtime timer
        this.binCapacity = problem.getCapacity();
        this.items = problem.items.flatten();

        // Initialize population
        List<Chromosome> population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            population.add(new Chromosome(createIndividual(items), binCapacity));
        }

        int generation = 0;
        int counter = 0;
        HashMap<Integer, Integer> iterationData = new HashMap<>();
        Chromosome fittestChromosome = Collections.min(population, Comparator.comparing(Chromosome::getFitness));
        while (counter < 30) {
            // Evolve population
            Collections.sort(population, (c1, c2) -> Integer.compare(c1.getFitness(), c2.getFitness()));
            population = evolvePopulation(population);
            generation++;
            Chromosome temp = Collections.min(population, Comparator.comparing(Chromosome::getFitness));
            if (temp == fittestChromosome) {
                counter++;
            } else {
                fittestChromosome = temp;
                counter = 0;
            }
            iterationData.put(generation, fittestChromosome.getFitness());
        }
        // System.out.println("Generation : " + generation);
        // Convert solution to list of bins
        fittestChromosome = Collections.min(population, Comparator.comparing(Chromosome::getFitness));
        Collections.shuffle(fittestChromosome.getGenes());
        int currentBinCapacity = 0;
        ItemFactory items = new ItemFactory();
        for (int gene : fittestChromosome.getGenes()) {
            if (currentBinCapacity + gene > binCapacity) {// bin full
                solution.bins.createBin(items); //add to list of bins
                items = new ItemFactory(); //create new bin
                currentBinCapacity = 0;
            }
            items.addItem(gene, 1); //add item to bin
            currentBinCapacity += gene;
        }

        solution.bins.createBin(items); //add final bin to list of bins
        return solution.finalizeResult(iterationData); // This should be at the bottom, it finalizes the result and calculates the runtime
    }

    private List<Integer> createIndividual(List<Integer> items) {
        List<Integer> individual = new ArrayList<>(items);
        Collections.shuffle(individual);
        return individual;
    }

    private List<Chromosome> evolvePopulation(List<Chromosome> population) {
        List<Chromosome> newPopulation = new ArrayList<>();

        // Elitism
        for (int i = 0; i < elitismCount; i++) {
            newPopulation.add(population.get(i));
        }

        // Crossover
        Random random = new Random();
        while (newPopulation.size() < populationSize) {
            Chromosome parent1 = selectParent(population);
            Chromosome parent2 = selectParent(population);
            if (random.nextDouble() < crossoverRate) {
                int crossoverPoint = random.nextInt(parent1.getGenes().size());

                List<Integer> childGenes = new ArrayList<>(parent1.getGenes().subList(0, crossoverPoint));
                List<Integer> parent2Copy = new ArrayList<>(parent2.getGenes());

                for (int gene : childGenes) {
                    int index = parent2Copy.indexOf(gene);
                    if (index != -1) {
                        parent2Copy.remove(index);
                    }
                }

                childGenes.addAll(parent2Copy);

                Chromosome child = new Chromosome(childGenes, binCapacity);
                newPopulation.add(child);
            } else {
                newPopulation.add(parent1);
            }
        }

        // Mutation
        for (Chromosome chromosome : newPopulation) {
            mutate(chromosome);
        }

        return newPopulation;
    }

    private Chromosome selectParent(List<Chromosome> population) {
        Random random = new Random();
        int index = random.nextInt(population.size());
        return population.get(index);
    }

    private void mutate(Chromosome chromosome) {
        Random random = new Random();
        if (random.nextDouble() < mutationRate) {
            List<Integer> genes = chromosome.getGenes();
            int index1 = random.nextInt(genes.size());
            int index2 = random.nextInt(genes.size());
            Collections.swap(genes, index1, index2);
            chromosome.calculateFitness(binCapacity);
        }
    }

}

class Chromosome {
    private List<Integer> genes;
    private int fitness;

    public Chromosome(List<Integer> genes, int binCapacity) {
        this.genes = genes;
        calculateFitness(binCapacity);
    }

    public List<Integer> getGenes() {
        return genes;
    }

    public int getFitness() {
        return fitness;
    }

    void calculateFitness(int binCapacity) {
        fitness = 0;
        int currentBinCapacity = 0;
        for (int gene : genes) {
            if (currentBinCapacity + gene > binCapacity) {
                fitness++;
                currentBinCapacity = 0;
            }
            currentBinCapacity += gene;
        }
        if (currentBinCapacity > 0) {
            fitness++;
        }
    }
}
