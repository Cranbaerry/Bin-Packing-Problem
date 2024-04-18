package factories;

import java.util.HashMap;

public class ItemFactory {
    private HashMap<Integer, Integer> items; // weight, quantity

    public ItemFactory() {
        this.items = new HashMap<>();
    }

    public HashMap<Integer, Integer> getItems() {
        return items;
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
            items.put(weight, items.get(weight) - quantity);
        }
    }

    public int getQuantity(int weight) {
        return items.get(weight);
    }

    public boolean containsItem(int weight) {
        return items.containsKey(weight);
    }

    public int getNumberOfItems() {
        return items.size();
    }

    public int getTotalWeight() {
        int capacity = 0;
        for (int weight : items.keySet()) {
            capacity += weight * items.get(weight);
        }
        return capacity;
    }
}
