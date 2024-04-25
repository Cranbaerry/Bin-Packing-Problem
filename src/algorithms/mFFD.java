//package algorithms;
//


package algorithms;

import factories.ItemFactory;
import objects.Problem;
import objects.Solution;

import java.util.*;

public class mFFD implements Algorithm {

    private List<Integer> largeItems;
    private List<Integer> mediumItems;
    private List<Integer> smallItems;
    private List<Integer> tinyItems;
    private int binCapacity;


    public mFFD() {
        // Initialize custom parameters here
        largeItems = new ArrayList<Integer>();
        mediumItems = new ArrayList<Integer>();
        smallItems = new ArrayList<Integer>();
        tinyItems = new ArrayList<Integer>();
    }

    @Override
    public Solution solve(Problem problem) {
        Solution solution = new Solution(problem); // This should be at the top, it initializes the runtime timer
        this.binCapacity = problem.getCapacity();
        HashMap<Integer, Integer> problemItems = problem.items.getItems();
        for (int weight : problemItems.keySet()) { // convert hashmap into array of integers
            for (int i = 0; i < problemItems.get(weight); i++) {
                if (weight > this.binCapacity / 2) {
                    largeItems.add(weight);
                } else if (weight > this.binCapacity / 3) {
                    mediumItems.add(weight);
                } else if (weight > this.binCapacity / 4) {
                    smallItems.add(weight);
                } else {
                    tinyItems.add(weight);
                }

            }
        }
        Collections.sort(largeItems, Collections.reverseOrder());
        Collections.sort(mediumItems, Collections.reverseOrder());
        Collections.sort(smallItems, Collections.reverseOrder());
        Collections.sort(tinyItems, Collections.reverseOrder());

        int currentBinCapacity = 0;
        HashMap<ItemFactory, Integer> bins = new HashMap<>();

        for (int weight : largeItems) {
            ItemFactory bin = new ItemFactory();
            bin.addItem(weight, 1);
            bins.put(bin, weight);
        }

        for (int weight : mediumItems) {
            boolean placed = false;
            for (Map.Entry<ItemFactory, Integer> entry : bins.entrySet()) {
                Integer capacity = entry.getValue();
                if (capacity + weight <= this.binCapacity) {
                    placed = true;
                    capacity += weight;
                    entry.setValue(capacity);
                }
            }

            if (placed == true) continue;
            ItemFactory bin = new ItemFactory();
            bin.addItem(weight, 1);
            bins.put(bin, weight);

        }

        for (int weight : smallItems) {
            boolean placed = false;
            for (Map.Entry<ItemFactory, Integer> entry : bins.entrySet()) {
                Integer capacity = entry.getValue();
                if (capacity + weight <= this.binCapacity) {
                    placed = true;
                    capacity += weight;
                    entry.setValue(capacity);
                }
            }

            if (placed == true) continue;
            ItemFactory bin = new ItemFactory();
            bin.addItem(weight, 1);
            bins.put(bin, weight);

        }

        for (int weight : tinyItems) {
            boolean placed = false;
            for (Map.Entry<ItemFactory, Integer> entry : bins.entrySet()) {
                Integer capacity = entry.getValue();
                if (capacity + weight <= this.binCapacity) {
                    placed = true;
                    capacity += weight;
                    entry.setValue(capacity);
                }
            }

            if (placed == true) continue;
            ItemFactory bin = new ItemFactory();
            bin.addItem(weight, 1);
            bins.put(bin, weight);

        }

        for (ItemFactory bin : bins.keySet()) {
            solution.bins.createBin(bin);
        }


        return solution.finalizeResult();
    }

}

