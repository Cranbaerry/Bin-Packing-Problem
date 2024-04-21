import algorithms.Algorithm;
import algorithms.GeneticAlgorithm;
import algorithms.FireFlyAlgorithm;
import factories.ItemFactory;
import objects.Problem;
import objects.Result;
import objects.Solution;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static final String PROBLEM_FILEPATH = "BPP.txt";
    public static final String RESULTS_FILEPATH = "results/results.csv";
    public static void main(String[] args) {
        List<Problem> problems = readBinPackingProblems(PROBLEM_FILEPATH);
        List<Result> results = new ArrayList<>();
        for (Problem problem : problems) {
            // Print problems
            System.out.println("Problem Name: " + problem.getName());
            System.out.println("Number of Items: " + problem.getNumberOfItems());
            System.out.println("Bin Capacity: " + problem.getCapacity());
            System.out.println();

            // Solve the problem with a genetic algorithm
            String algorithmName = "Genetic Algorithm";
            Algorithm geneticAlgorithm = new GeneticAlgorithm();
            Solution solution = geneticAlgorithm.solve(problem);
            ArrayList<ItemFactory> bins = solution.bins.getBins();
            Result result = solution.evaluateResult(problem.getName(), algorithmName);
            result.printOut();
            result.plotGraph(bins);
            results.add(result);
            
            algorithmName = "Firefly Algorithm";
            Algorithm fireflyAlgorithm = new FireFlyAlgorithm();
            solution = fireflyAlgorithm.solve(problem);
            bins = solution.bins.getBins();
            result = solution.evaluateResult(problem.getName(), algorithmName);
            result.printOut();
            result.plotGraph(bins);
            results.add(result);


            // Other algorithms goes here


            // Put this at the end for spacing new line
            System.out.println();
        }

        // Write results to CSV
        saveResults(results, RESULTS_FILEPATH);
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
            bw.write("Problem Name,Algorithm,Execution Time (ms),Number Of Bins,Bin Fullness (%),Fairness Of Packing");
            bw.newLine();

            for (Result result : results) {
                String csvLine = String.format(
                        "\"%s\",%s,%d,%d,%.3f,%.3f",
                        result.getProblemName().replaceAll("\"", "\"\""),
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