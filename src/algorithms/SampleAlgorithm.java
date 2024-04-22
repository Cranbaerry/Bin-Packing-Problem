package algorithms;

import factories.ItemFactory;
import objects.Problem;
import objects.Solution;

import java.util.HashMap;

public class SampleAlgorithm implements Algorithm {
    private int populationSize;
    private double mutationRate;
    private double crossoverRate;
    private int elitismCount;


    public SampleAlgorithm() {
        // Initialize custom parameters here
        this.populationSize = 0;
        this.mutationRate = 0;
        this.crossoverRate = 0;
        this.elitismCount = 0;
        // ...
    }

    @Override
    public Solution solve(Problem problem) {
        Solution solution = new Solution(problem); // This should be at the top, it initializes the runtime timer
        while (solution.getCurrentRuntime() < 1000L) {
            // Implement the genetic algorithm here
            // ...
            // Example of how to access the problem data
            HashMap<Integer, Integer> problemItems = problem.items.getItems();
            for (int weight : problemItems.keySet()) {
                // System.out.println("Weight: " + weight + ", Quantity: " + problemItems.get(weight));
            }

            // Example of how to create a bin and add an item to it
            ItemFactory items = new ItemFactory(); // This is list of items
            items.addItem(1, 1); // Add an item with weight 1 and quantity 1
            items.addItem(1, 2); // Add an item with weight 1 and quantity 2
            // NOTE: If an item with the same weight already exists, the quantity will be increased

            // Print the total weight of the items
            // First item: Weight 1, Quantity 1 -> 1 * 1 = 1
            // Second item: Weight 1, Quantity 2 -> 1 * 2 = 2
            // Total weight: 3
            // System.out.println("Total weight: " + items.getTotalWeight());

            // Create a bin with the set of items
            solution.bins.createBin(items);

            // If you want to add more bins, create a new set of items and create a new bin

            // Finalize the result
            break;
        }

        return solution.finalizeResult(); // This should be at the bottom, it finalizes the result and calculates the runtime
    }
}
