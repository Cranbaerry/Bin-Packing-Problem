package objects;

import factories.BinFactory;

public class Solution {
    public BinFactory bins;
    private long startTime, endTime;
    private Problem problem;

    public Solution(Problem problem) {
        this.startTime = System.currentTimeMillis();
        this.problem = problem;
        this.bins = new BinFactory(problem.getCapacity());
    }

    public Solution finalizeResult() {
        this.endTime = System.currentTimeMillis() - this.startTime;
        return this;
    }

    public Result evaluateResult(String problemName, String algorithmName) {
        Result result = new Result();
        result.setAlgorithmName(algorithmName);
        result.setProblemName(problemName);
        result.setNumberOfBins(this.bins.getNumberOfBins());
        result.setRuntime(this.getTotalRuntime());
        result.setBinFullness(this.bins.getBinFullness());
        result.setFairnessOfPacking(this.bins.getBinFullnessStdDev());

        return result;
    }

    public long getCurrentRuntime() {
        return System.currentTimeMillis() - this.startTime;
    }

    public long getTotalRuntime() {
        return endTime;
    }
}
