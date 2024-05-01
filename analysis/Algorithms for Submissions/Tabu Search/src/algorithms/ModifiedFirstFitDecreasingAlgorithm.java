package algorithms;

import factories.ItemFactory;
import objects.Problem;
import objects.Solution;

import java.util.*;

public class ModifiedFirstFitDecreasingAlgorithm implements Algorithm {

    private List<Integer> largeItems;
    private List<Integer> mediumItems;
    private List<Integer> smallItems;
    private List<Integer> tinyItems;
    private int binCapacity;

    public ModifiedFirstFitDecreasingAlgorithm() {
    }

    @Override
    public Solution solve(Problem problem) {
        Solution solution = new Solution(problem); // This should be at the top, it initializes the runtime timer
        this.binCapacity = problem.getCapacity();
        this.largeItems = new ArrayList<>();
        this.mediumItems = new ArrayList<>();
        this.smallItems = new ArrayList<>();
        this.tinyItems = new ArrayList<>();
        HashMap<Integer, Integer> problemItems = problem.items.getItems();
        int count = 0;

        for (Map.Entry<Integer, Integer> entry : problemItems.entrySet()) {
            int weight = entry.getKey();
            for (int i = 0; i < entry.getValue(); i++) {
                if (weight > this.binCapacity / 2) {
                    largeItems.add(weight);
                    count++;
                } else if (weight > this.binCapacity / 3) {
                    mediumItems.add(weight);
                    count++;
                } else if (weight > this.binCapacity / 4) {
                    smallItems.add(weight);
                    count++;
                } else if (weight <= this.binCapacity / 4) {
                    tinyItems.add(weight);
                    count++;
                }
            }
        }

        largeItems.sort(Collections.reverseOrder());
        mediumItems.sort(Collections.reverseOrder());
        smallItems.sort(Collections.reverseOrder());
        tinyItems.sort(Collections.reverseOrder());

        ArrayList<Integer> weights = new ArrayList<Integer>();
        ArrayList<ItemFactory> bins = new ArrayList<ItemFactory>();
        int itemIterated = 0;
        for (int weight : largeItems) {
            ItemFactory bin = new ItemFactory();
            bin.addItem(weight, 1);
            bins.add(bin);
            weights.add(weight);
            itemIterated++;
        }

        for (int weight : mediumItems) {
            boolean placed = false;
            for (int i = 0; i < bins.size(); i++) {
                Integer capacity = weights.get(i);
                if (capacity + weight <= this.binCapacity) {
                    placed = true;
                    capacity += weight;
                    ItemFactory bin = bins.get(i);
                    bin.addItem(weight, 1);
                    bins.set(i, bin);
                    weights.set(i, capacity);
                    itemIterated++;
                    break;
                }
            }

            if (!placed) {
                ItemFactory bin = new ItemFactory();
                bin.addItem(weight, 1);
                bins.add(bin);
                weights.add(weight);
                itemIterated++;
            }

        }

        for (int weight : smallItems) {
            boolean placed = false;
            for (int i = 0; i < bins.size(); i++) {
                Integer capacity = weights.get(i);
                if (capacity + weight <= this.binCapacity) {
                    placed = true;
                    capacity += weight;
                    ItemFactory bin = bins.get(i);
                    bin.addItem(weight, 1);
                    bins.set(i, bin);
                    weights.set(i, capacity);
                    itemIterated++;
                    break;
                }
            }

            if (!placed) {
                ItemFactory bin = new ItemFactory();
                bin.addItem(weight, 1);
                bins.add(bin);
                weights.add(weight);
                itemIterated++;
            }
        }

        for (int weight : tinyItems) {
            boolean placed = false;
            for (int i = 0; i < bins.size(); i++) {
                Integer capacity = weights.get(i);
                if (capacity + weight <= this.binCapacity) {
                    placed = true;
                    capacity += weight;
                    ItemFactory bin = bins.get(i);
                    bin.addItem(weight, 1);
                    bins.set(i, bin);
                    weights.set(i, capacity);
                    itemIterated++;
                    break;
                }
            }

            if (!placed) {
                ItemFactory bin = new ItemFactory();
                bin.addItem(weight, 1);
                bins.add(bin);
                weights.add(weight);
                itemIterated++;
            }

        }

        for (int i = 0; i < bins.size(); i++) {
            ItemFactory bin = bins.get(i);
            solution.bins.createBin(bin);
        }

        // Since first fit is technically run once
        // We can just add the number of bins to the iteration data 30 times
        // This is a hacky way to make the graph look nice
        HashMap<Integer, Integer> iterationData = new HashMap<>();
        for (int i = 1; i <= 30; i++) {
            iterationData.put(i, solution.bins.getNumberOfBins());
        }

        return solution.finalizeResult(iterationData);
    }
}
