//package algorithms;
//


package algorithms;

import factories.ItemFactory;
import objects.Problem;
import objects.Solution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class FirstFitDescending implements Algorithm {
    private List<Integer> items;
    private int binCapacity;

    public FirstFitDescending() {
        // Initialize custom parameters here
        items = new ArrayList<Integer>();
    }

    @Override
    public Solution solve(Problem problem) {
        Solution solution = new Solution(problem); // This should be at the top, it initializes the runtime timer
        this.binCapacity = problem.getCapacity();
        HashMap<Integer, Integer> problemItems = problem.items.getItems();
        this.items = problem.items.flatten();
        items.sort(Collections.reverseOrder());

        int currentBinCapacity = 0;
        ItemFactory bin = new ItemFactory();

        for (int weight : items) {
            if (currentBinCapacity + weight > binCapacity) {
                solution.bins.createBin(bin);
                currentBinCapacity = 0;
                bin = new ItemFactory();
            }
            bin.addItem(weight, 1);
            currentBinCapacity += weight;
        }
        solution.bins.createBin(bin);

        return solution.finalizeResult();
    }
}

