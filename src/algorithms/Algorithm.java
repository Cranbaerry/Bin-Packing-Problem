package algorithms;

import objects.Problem;
import objects.Solution;

public interface Algorithm {
    public Solution solve(Problem problem) throws CloneNotSupportedException;
}
