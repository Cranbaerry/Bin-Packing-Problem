//package algorithms;
//
//import factories.ItemFactory;
//import objects.Problem;
//import objects.Solution;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Random;
//
//public class GeneticAlgorithm implements Algorithm {
//    private int populationSize;
//    private double mutationRate;
//    private double crossoverRate;
//    private int elitismCount;
//    private List<Integer> items;
//    private int binCapacity;
//
//
//    public GeneticAlgorithm() {
//        // Initialize custom parameters here
//        this.populationSize = 100;
//        this.mutationRate = 0.01;
//        this.crossoverRate = 0.8;
//        this.elitismCount = 3;
//        items = new ArrayList<Integer>();
//    }
//
//    @Override
//    public Solution solve(Problem problem, int timeLimit) {
//        Solution solution = new Solution(problem); // This should be at the top, it initializes the runtime timer
//        this.binCapacity = problem.getCapacity();
//        while (solution.getCurrentRuntime() < timeLimit * 1000L) {
//            HashMap<Integer, Integer> problemItems = problem.items.getItems();
//            for (int weight : problemItems.keySet()) { // convert hashmap into array of integers
//            	for(int i = 0; i < problemItems.get(weight);i++) {
//            		items.add(weight);
//            	}
//            }
//            
//            int chromosomeLength = items.size();
//
//            // Initialize population
//            List<Chromosome> population = new ArrayList<>();
//            for (int i = 0; i < populationSize; i++) {
//                population.add(new Chromosome(createIndividual(items, chromosomeLength),binCapacity));
//            }
//
//            // Evolve population
//            for (int generation = 0; generation < 100; generation++) { // Adjust number of generations as needed
//                Collections.sort(population, (c1, c2) -> Integer.compare(c1.getFitness(), c2.getFitness()));
//                population = evolvePopulation(population);
//            }
//
//            // Convert solution to list of bins
//            Chromosome fittestChromosome = Collections.min(population, Comparator.comparing(Chromosome::getFitness));
//            int currentBinCapacity = 0;
//            ItemFactory items = new ItemFactory();
//   	     	for (int gene : fittestChromosome.getGenes()) {
//	   	         if (currentBinCapacity + gene > binCapacity) {// bin full
//	   	        	 solution.bins.createBin(items); //add to list of bins
//	   	             items = new ItemFactory(); //create new bin
//	   	             currentBinCapacity = 0;
//	   	         }
//	   	         items.addItem(gene, 1); //add item to bin
//	   	         currentBinCapacity += gene;
//	   	     }
//   	     	
//   	     	solution.bins.createBin(items); //add final bin to list of bins
//
//            // Example of how to create a bin and add an item to it
////            ItemFactory items = new ItemFactory(); // This is list of items
////            items.addItem(1, 1); // Add an item with weight 1 and quantity 1
////            items.addItem(1, 2); // Add an item with weight 1 and quantity 2
//            // NOTE: If an item with the same weight already exists, the quantity will be increased
//
//            // Print the total weight of the items
//            // First item: Weight 1, Quantity 1 -> 1 * 1 = 1
//            // Second item: Weight 1, Quantity 2 -> 1 * 2 = 2
//            // Total weight: 3
//            // System.out.println("Total weight: " + items.getTotalWeight());
//
//            // Create a bin with the set of items
////            solution.bins.createBin(items);
//
//            // If you want to add more bins, create a new set of items and create a new bin
//
//            // Finalize the result
//            break;
//        }
//
//        return solution.finalizeResult(); // This should be at the bottom, it finalizes the result and calculates the runtime
//    }
//    
//    private List<Integer> createIndividual(List<Integer> items, int chromosomeLength) {
//        List<Integer> individual = new ArrayList<>(items);
//        Collections.shuffle(individual);
//        return individual;
//    }
//
//    private List<Chromosome> evolvePopulation(List<Chromosome> population) {
//        List<Chromosome> newPopulation = new ArrayList<>();
//
//        // Elitism
//        for (int i = 0; i < elitismCount; i++) {
//            newPopulation.add(population.get(i));
//        }
//
//        // Crossover
//        Random random = new Random();
//        while (newPopulation.size() < populationSize) {
//            Chromosome parent1 = selectParent(population);
//            Chromosome parent2 = selectParent(population);
//            if (random.nextDouble() < crossoverRate) {
//                int crossoverPoint = random.nextInt(parent1.getGenes().size());
//
//                List<Integer> childGenes = new ArrayList<>(parent1.getGenes().subList(0, crossoverPoint));
//                List<Integer> parent2Copy = new ArrayList<>(parent2.getGenes());
//
//                for (int gene : childGenes) {
//                    int index = parent2Copy.indexOf(gene);
//                    if (index != -1) {
//                        parent2Copy.remove(index);
//                    }
//                }
//
//                childGenes.addAll(parent2Copy);
//
//                Chromosome child = new Chromosome(childGenes,binCapacity);
//                newPopulation.add(child);
//            } else {
//                newPopulation.add(parent1);
//            }
//        }
//
//        // Mutation
//        for (Chromosome chromosome : newPopulation) {
//            mutate(chromosome);
//        }
//
//        return newPopulation;
//    }
//
//    private Chromosome selectParent(List<Chromosome> population) {
//        Random random = new Random();
//        int index = random.nextInt(population.size());
//        return population.get(index);
//    }
//
//    private void mutate(Chromosome chromosome) {
//        Random random = new Random();
//        if (random.nextDouble() < mutationRate) {
//            List<Integer> genes = chromosome.getGenes();
//            int index1 = random.nextInt(genes.size());
//            int index2 = random.nextInt(genes.size());
//            Collections.swap(genes, index1, index2);
//            chromosome.calculateFitness(binCapacity);
//        }
//    }
//
//}
//
//class Chromosome {
//    private List<Integer> genes;
//    private int fitness;
//
//    public Chromosome(List<Integer> genes, int binCapacity) {
//        this.genes = genes;
//        calculateFitness(binCapacity);
//    }
//
//    public List<Integer> getGenes() {
//        return genes;
//    }
//
//    public int getFitness() {
//        return fitness;
//    }
//
//    void calculateFitness(int binCapacity) {
//        fitness = 0;
//        int currentBinCapacity = 0;
//        for (int gene : genes) {
//            if (currentBinCapacity + gene > binCapacity) {
//                fitness++;
//                currentBinCapacity = 0;
//            }
//            currentBinCapacity += gene;
//        }
//    }
//}

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
        items = new ArrayList<Integer>();
    }

    @Override
    public Solution solve(Problem problem, int timeLimit) {
        Solution solution = new Solution(problem); // This should be at the top, it initializes the runtime timer
        this.binCapacity = problem.getCapacity();
        HashMap<Integer, Integer> problemItems = problem.items.getItems();
        for (int weight : problemItems.keySet()) { // convert hashmap into array of integers
        	for(int i = 0; i < problemItems.get(weight);i++) {
        		items.add(weight);
        	}
        }
        
        int chromosomeLength = items.size();
        
        // Initialize population
        List<Chromosome> population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
        	population.add(new Chromosome(createIndividual(items, chromosomeLength),binCapacity));
        }
        
        int generation=0;
        while (solution.getCurrentRuntime() < timeLimit * 1000L) {
        	// Evolve population
        	Collections.sort(population, (c1, c2) -> Integer.compare(c1.getFitness(), c2.getFitness()));
        	population = evolvePopulation(population);
        	generation++;
        }
        System.out.println("Generation : " + generation);
        // Convert solution to list of bins
        Chromosome fittestChromosome = Collections.min(population, Comparator.comparing(Chromosome::getFitness));
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

        return solution.finalizeResult(); // This should be at the bottom, it finalizes the result and calculates the runtime
    }
    
    private List<Integer> createIndividual(List<Integer> items, int chromosomeLength) {
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

                Chromosome child = new Chromosome(childGenes,binCapacity);
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
    }
}