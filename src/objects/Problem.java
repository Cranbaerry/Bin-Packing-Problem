package objects;

import factories.ItemFactory;

public class Problem {
    public ItemFactory items;
    private String name;
    private int numberOfItems, numberOfData;
    private int capacity;

    public Problem() {
        items = new ItemFactory();
        numberOfItems = 0;
        numberOfData = 0;
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

    public int getNumberOfData() {
        return numberOfData;
    }

    public void setNumberOfData(int numberOfData) {
        this.numberOfData = numberOfData;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
