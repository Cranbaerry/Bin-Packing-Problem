package objects;

import factories.ItemFactory;

public class Problem {
    public ItemFactory items;
    private String name;
    private int numberOfItems;
    private int capacity;

    public Problem() {
        items = new ItemFactory();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfItems() {
        return numberOfItems;
    }

    public void setNumberOfItems(int numberOfItems) {
        this.numberOfItems = numberOfItems;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
