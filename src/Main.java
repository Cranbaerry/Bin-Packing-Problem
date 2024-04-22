import algorithms.Algorithm;
import algorithms.FireFlyAlgorithm;
import algorithms.GeneticAlgorithm;
import algorithms.TabuSearchAlgorithm;
import factories.ItemFactory;
import objects.Problem;
import objects.Result;
import objects.Solution;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static final String PROBLEM_FILEPATH = "BPP.txt";
    public static final String RESULTS_FILEPATH = "results/results.csv";

    public static void main(String[] args) throws CloneNotSupportedException {
        List<Problem> problems = readBinPackingProblems(PROBLEM_FILEPATH);
        List<Result> results = new ArrayList<>();
        Map<String, Algorithm> algorithms = new HashMap<>();
        algorithms.put("Genetic Algorithm", new GeneticAlgorithm());
        algorithms.put("Firefly Algorithm", new FireFlyAlgorithm());
        algorithms.put("Tabu Search Algorithm", new TabuSearchAlgorithm());
        algorithms.put("First Fit Descending", new FirstFitDescending());
        // algorithms.put("Dynamic Tabu Search Algorithm", new DynamicTabuSearchAlgorithm());
        // algorithms.put("Simulated Annealing Algorithm", new SimulatedAnnealingAlgorithm());
        // TODO: Add other algorithms here

        System.out.println("Bin Packing Problem Solver");
        System.out.printf("Found %d problems in %s%n", problems.size(), PROBLEM_FILEPATH);
        System.out.printf("Using %d algorithms:%n", algorithms.size());
        for (String algorithmName : algorithms.keySet()) {
            System.out.printf("- %s%n", algorithmName);
        }
        for (Problem problem : problems) {
            System.out.printf("%nSolving problem %s with %d items and bin capacity %d..%n",
                    problem.getName(), problem.getNumberOfItems(), problem.getCapacity());

            for (Map.Entry<String, Algorithm> entry : algorithms.entrySet()) {
                String algorithmName = entry.getKey();
                Algorithm algorithm = entry.getValue();

                Solution solution = algorithm.solve(problem);
                ArrayList<ItemFactory> bins = solution.bins.getBins();
                Result result = solution.evaluateResult(problem.getName(), algorithmName);
                result.printOut(); // Commented out to reduce output

                result.plotGraph(bins);
                results.add(result);
                System.out.print("✔ Finished " + algorithmName + " in " + result.getRuntime() + "ms\n");
            }
        }

        saveResults(results, RESULTS_FILEPATH);

        long totalRuntime = results.stream().mapToLong(Result::getRuntime).sum();
        System.out.printf("%nAll problems solved in %dms%n", totalRuntime);
        System.out.println("Results saved to " + RESULTS_FILEPATH);
    }

    public static List<Problem> readBinPackingProblems(String filename) {
        List<Problem> problems = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            Problem problem = null;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue; // Skip empty lines
                if (line.startsWith("'")) {
                    // New problem
                    if (problem != null) {
                        problems.add(problem);
                    }
                    problem = new Problem();
                    String name = line.substring(1).trim();
                    name = name.substring(0, name.length() - 1); // Remove the last '
                    problem.setName(name);
                } else {
                    // Reading problem data
                    if (problem != null) {
                        if (problem.getNumberOfItems() == 0) {
                            problem.setNumberOfItems(Integer.parseInt(line.trim()));
                        } else if (problem.getCapacity() == 0) {
                            problem.setCapacity(Integer.parseInt(line.trim()));
                        } else {
                            String[] parts = line.trim().split("\\s+");
                            int weight = Integer.parseInt(parts[0]);
                            int count = Integer.parseInt(parts[1]);
                            problem.items.addItem(weight, count);
                        }
                    }
                }
            }
            if (problem != null) {
                problems.add(problem);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return problems;
    }

    public static void saveResults(List<Result> results, String csvFile) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile))) {
            bw.write("Problem Name, Number of Items, Bin Capacity, Algorithm,Execution Time (ms),Number Of Bins,Bin Fullness (%),Fairness Of Packing");
            bw.newLine();

            for (Result result : results) {
                String csvLine = String.format(
                        "\"%s\",%d, %d, %s,%d,%d,%.3f,%.3f",
                        result.getProblemName().replaceAll("\"", "\"\""),
                        result.getNumberOfItems(),
                        result.getBinCapacity(),
                        result.getAlgorithmName(),
                        result.getRuntime(),
                        result.getNumberOfBins(),
                        result.getBinFullness(),
                        result.getFairnessOfPacking()
                );
                bw.write(csvLine);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
