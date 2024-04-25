//package algorithms;
//


package algorithms;

import factories.ItemFactory;
import objects.Problem;
import objects.Solution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class mFFD implements Algorithm {

    private List<Integer> largeItems;
    private List<Integer> mediumItems;
    private List<Integer> smallItems;
    private List<Integer> tinyItems;
    private int binCapacity;


    public mFFD() {
        // Initialize custom parameters here
        
    }

    @Override
    public Solution solve(Problem problem) {
        Solution solution = new Solution(problem); // This should be at the top, it initializes the runtime timer
        this.binCapacity = problem.getCapacity();
        HashMap<Integer, Integer> problemItems = problem.items.getItems();
        int count = 0;
        largeItems = new ArrayList<Integer>();
        mediumItems = new ArrayList<Integer>();
        smallItems = new ArrayList<Integer>();
        tinyItems = new ArrayList<Integer>();
        for(Map.Entry<Integer, Integer> entry: problemItems.entrySet()) {
        	int weight = entry.getKey();
        	for(int i = 0; i < entry.getValue(); i++) {
        		if(weight > this.binCapacity / 2) {
        			largeItems.add(weight);
        			count++;
        		}
        		else if(weight > this.binCapacity / 3) {
        			mediumItems.add(weight);
        			count++;
        		}
        		else if(weight > this.binCapacity / 4) {
        			smallItems.add(weight);
        			count++;
        		}
        		else if(weight <= this.binCapacity / 4){
        			tinyItems.add(weight);
        			count++;
        		}
        	}
        }
        Collections.sort(largeItems, Collections.reverseOrder());
        Collections.sort(mediumItems, Collections.reverseOrder());
        Collections.sort(smallItems, Collections.reverseOrder());
        Collections.sort(tinyItems, Collections.reverseOrder());
        
        
        ArrayList<Integer> weights = new ArrayList<Integer>();
        ArrayList<ItemFactory> bins = new ArrayList<ItemFactory>();
//        HashMap<ItemFactory, Integer> bins = new HashMap<>();
        int itemIterated = 0;
        for(int weight: largeItems){
            ItemFactory bin = new ItemFactory();
            bin.addItem(weight, 1);
            bins.add(bin);
            weights.add(weight);
            itemIterated++;
        }
        
        for(int weight: mediumItems) {
        	boolean placed = false;
        	for(int i = 0; i < bins.size(); i++) {
        		Integer capacity = weights.get(i);
        		if(capacity + weight <= this.binCapacity) {
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
        	
        	if(!placed) {
        		ItemFactory bin = new ItemFactory();
                bin.addItem(weight, 1);
                bins.add(bin);
                weights.add(weight);
                itemIterated++;
        	}
        	
        }
        
        for(int weight: smallItems) {
        	boolean placed = false;
        	for(int i = 0; i < bins.size(); i++) {
        		Integer capacity = weights.get(i);
        		if(capacity + weight <= this.binCapacity) {
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
        	
        	if(!placed) {
        		ItemFactory bin = new ItemFactory();
                bin.addItem(weight, 1);
                bins.add(bin);
                weights.add(weight);
                itemIterated++;
        	}
        }
        
        for(int weight: tinyItems) {
        	boolean placed = false;
        	for(int i = 0; i < bins.size(); i++) {
        		Integer capacity = weights.get(i);
        		if(capacity + weight <= this.binCapacity) {
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
        	
        	if(!placed) {
        		ItemFactory bin = new ItemFactory();
                bin.addItem(weight, 1);
                bins.add(bin);
                weights.add(weight);
                itemIterated++;
        	}
        	
        }
        
        System.out.println(itemIterated);
        
        for(int i = 0; i < bins.size(); i++) {
        	ItemFactory bin = bins.get(i);
//        	System.out.println(bin.getNumberOfItems());
        	solution.bins.createBin(bin);
        }
        
        
        return solution.finalizeResult();
    }

}

