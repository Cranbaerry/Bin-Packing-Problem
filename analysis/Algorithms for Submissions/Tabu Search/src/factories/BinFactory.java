package factories;

import java.util.ArrayList;

public class BinFactory implements Cloneable {
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

    public double getTotalWeight() {
        int totalWeight = 0;
        for (ItemFactory bin : bins) {
            totalWeight += bin.getTotalWeight();
        }
        return totalWeight;
    }

    // SwapItem between two bins
    public void swapItem(int binIndex1, int binIndex2, int itemIndex1, int itemIndex2) {
        ItemFactory bin1 = bins.get(binIndex1);
        ItemFactory bin2 = bins.get(binIndex2);
        int weight1 = bin1.getItems().get(itemIndex1);
        int weight2 = bin2.getItems().get(itemIndex2);
        bin1.removeItem(weight1, 1);
        bin2.removeItem(weight2, 1);
        bin1.addItem(weight2, 1);
        bin2.addItem(weight1, 1);
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

    @Override
    public BinFactory clone() {
        BinFactory clone = new BinFactory(this.maxCapacity);
        for (ItemFactory bin : bins) {
            clone.bins.add(bin.clone());
        }
        return clone;
    }
}
