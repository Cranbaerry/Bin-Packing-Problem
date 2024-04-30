package objects;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Result {
    private String algorithmName, problemName;
    private int numberOfBins, numberOfItems, binCapacity;
    private long runtime;
    private double binFullness;
    private double fairnessOfPacking;
    private double solutionFitness;

    private HashMap<Integer, Integer> iterationData;

    public Result() {
        this.algorithmName = "";
        this.numberOfBins = 0;
        this.runtime = 0;
        this.binFullness = 0;
        this.fairnessOfPacking = 0;
        this.solutionFitness = 0;
        this.iterationData = new HashMap<>();
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName;
    }

    public int getNumberOfItems() {
        return numberOfItems;
    }

    public void setNumberOfItems(int numberOfItems) {
        this.numberOfItems = numberOfItems;
    }

    public int getBinCapacity() {
        return binCapacity;
    }

    public void setBinCapacity(int binCapacity) {
        this.binCapacity = binCapacity;
    }

    public double getFitness() {
        return solutionFitness;
    }

    public void setFitness(double fitness) {
        this.solutionFitness = fitness;
    }

    public String getProblemName() {
        return problemName;
    }

    public void setProblemName(String problemName) {
        this.problemName = problemName;
    }

    public int getNumberOfBins() {
        return numberOfBins;
    }

    public void setNumberOfBins(int numberOfBins) {
        this.numberOfBins = numberOfBins;
    }

    public long getRuntime() {
        return runtime;
    }

    public void setRuntime(long runtime) {
        this.runtime = runtime;
    }

    public double getBinFullness() {
        return binFullness;
    }

    public void setBinFullness(double binFullness) {
        this.binFullness = binFullness;
    }

    public double getFairnessOfPacking() {
        return fairnessOfPacking;
    }

    public void setFairnessOfPacking(double fairnessOfPacking) {
        this.fairnessOfPacking = fairnessOfPacking;
    }

    public HashMap<Integer, Integer> getIterationData() {
        return iterationData;
    }

    public void setIterationData(HashMap<Integer, Integer> iterationData) {
        this.iterationData = iterationData;
    }

    public void printIterationData() {
        System.out.println("Iteration Data:");
        for (Map.Entry<Integer, Integer> entry : iterationData.entrySet()) {
            System.out.printf("Iteration %d: %d bins%n", entry.getKey(), entry.getValue());
        }
        System.out.println();
    }

    public void printOut() {
        System.out.printf("Results for %s using %s%n", problemName, algorithmName);
        System.out.printf("\uD83D\uDC49 Number of Bins: %d%n", numberOfBins);
        System.out.printf("\uD83D\uDC49 Execution Time: %dms%n", runtime);
        System.out.printf("\uD83D\uDC49 Bin Fullness: %.3f%n", binFullness);
        System.out.printf("\uD83D\uDC49 Fairness of Packing: %.3f%n", fairnessOfPacking);
        System.out.println();
    }

    // Method to generate random colors
    private Color getRandomColor() {
        return new Color((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256));
    }
}
