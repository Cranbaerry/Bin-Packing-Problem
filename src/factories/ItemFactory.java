package factories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemFactory implements Cloneable {
    private HashMap<Integer, Integer> items; // weight, quantity

    public ItemFactory() {
        this.items = new HashMap<>();
    }

    public HashMap<Integer, Integer> getItems() {
        return items;
    }

    public ArrayList<Integer> getItemsFlattened() {
        ArrayList<Integer> itemsList = new ArrayList<>();
        for (int weight : items.keySet()) {
            for (int i = 0; i < items.get(weight); i++) {
                itemsList.add(weight);
            }
        }
        return itemsList;
    }

    public int getNumberOfItems() {
        return this.getItemsFlattened().size();
    }

    public void clearItems() {
        items.clear();
    }

    public void addItem(int weight, int quantity) {
        // Check if the item already exists
        if (items.containsKey(weight)) {
            items.put(weight, items.get(weight) + quantity);
        } else {
            items.put(weight, quantity);
        }
    }

    public void removeItem(int weight, int quantity) {
        // Check if the item already exists
        if (items.containsKey(weight)) {
            // System.out.println("Removing " + quantity + " items of weight " + weight);
            items.put(weight, items.get(weight) - quantity);
            if (items.get(weight) <= 0) {
                items.remove(weight);
            }
        }
    }

    public int getQuantity(int weight) {
        return items.get(weight);
    }

    public boolean containsItem(int weight) {
        return items.containsKey(weight);
    }

    public int getTotalWeight() {
        int capacity = 0;
        for (int weight : items.keySet()) {
            capacity += weight * items.get(weight);
        }
        return capacity;
    }

    public List<Integer> flatten() {
        ArrayList<Integer> flattened = new ArrayList<>();
        for (int weight : items.keySet()) {
            for (int i = 0; i < items.get(weight); i++) {
                flattened.add(weight);
            }
        }
        return flattened;
    }

    @Override
    public ItemFactory clone() {
        ItemFactory clone = new ItemFactory();
        for (int weight : items.keySet()) {
            clone.addItem(weight, items.get(weight));
        }
        return clone;
    }
}
