package objects;

import factories.BinFactory;
import factories.ItemFactory;

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

    public long getCurrentRuntime() {
        return System.currentTimeMillis() - this.startTime;
    }
    public long getTotalRuntime() {
        return endTime;
    }
}
