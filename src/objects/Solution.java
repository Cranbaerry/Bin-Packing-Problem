package objects;

import factories.BinFactory;

public class Solution implements Cloneable {
    private final Problem problem;
    public BinFactory bins;
    private long startTime, endTime;

    public Solution(Problem problem) {
        this.startTime = System.currentTimeMillis();
        this.problem = problem;
        this.bins = new BinFactory(problem.getCapacity());
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public Solution finalizeResult() {
        this.endTime = System.currentTimeMillis() - this.startTime;
        return this;
    }

    public Result evaluateResult(String problemName, String algorithmName) {
        Result result = new Result();
        result.setProblemName(problemName);
        result.setNumberOfItems(this.problem.getNumberOfItems());
        result.setBinCapacity(this.problem.getCapacity());
        result.setAlgorithmName(algorithmName);
        result.setNumberOfBins(this.bins.getNumberOfBins());
        result.setRuntime(this.getTotalRuntime());
        result.setBinFullness(this.bins.getBinFullness());
        result.setFairnessOfPacking(this.bins.getBinFullnessStdDev());
        result.setFitness(this.getFitness());

        return result;
    }

    public long getCurrentRuntime() {
        return System.currentTimeMillis() - this.startTime;
    }

    public long getTotalRuntime() {
        return endTime;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Solution clone = (Solution) super.clone();
        clone.bins = this.bins.clone();
        return clone;
    }

    // Calculate the fitness as the ratio of the total weight to the number of bins.
    public double getFitness() {
        // return this.bins.getBinFullness();
        return this.bins.getTotalWeight() / this.bins.getNumberOfBins();
    }
}

