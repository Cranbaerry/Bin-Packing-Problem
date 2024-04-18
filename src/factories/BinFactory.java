package factories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BinFactory {
    private ArrayList<ItemFactory> bins;
    private int maxCapacity;

    public BinFactory(int maxCapacity) {
        this.bins = new ArrayList<>();
        this.maxCapacity = maxCapacity;
    }

    public ArrayList<ItemFactory> getBins() {
        return bins;
    }

    public void clearBins() {
        bins.clear();
    }

    public void createBin(ItemFactory items) {
        if (items.getTotalWeight() > maxCapacity) {
            throw new IllegalArgumentException("Bin weight exceeds the maximum capacity");
        }
        bins.add(items);
    }

    public int getNumberOfBins() {
        return bins.size();
    }
}
