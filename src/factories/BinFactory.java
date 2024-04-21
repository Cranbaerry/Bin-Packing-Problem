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

    public double getBinFullness() {
        double fullness = 0;
        for (ItemFactory bin : bins) {
            fullness += (double) bin.getTotalWeight() / maxCapacity;
        }
        return fullness / bins.size() * 100;
    }

    public double getBinFullnessVariance() {
        // First calculate the mean (average) fullness of bins
        double meanFullness = 0;
        if (bins.isEmpty()) return 0; // Avoid division by zero

        for (ItemFactory bin : bins) {
            meanFullness += (double) bin.getTotalWeight() / maxCapacity;
        }
        meanFullness /= bins.size();

        // Calculate the sum of squared differences from the mean
        double sumSquaredDifferences = 0;
        for (ItemFactory bin : bins) {
            double fullness = (double) bin.getTotalWeight() / maxCapacity;
            sumSquaredDifferences += Math.pow(fullness - meanFullness, 2);
        }

        // Return the variance
        return sumSquaredDifferences / bins.size();
    }

    public double getBinFullnessStdDev() {
        return Math.sqrt(getBinFullnessVariance());
    }
}
