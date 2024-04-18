package algorithms;

import objects.Problem;
import objects.Solution;

public interface Algorithm {
    public Solution solve(Problem problem, int timeLimit); // Time limit in seconds
}
