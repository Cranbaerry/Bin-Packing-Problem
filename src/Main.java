import algorithms.*;
import factories.ItemFactory;
import objects.Problem;
import objects.Result;
import objects.Solution;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {
    private static final String PROBLEM_FILEPATH = "BPP.txt";
    private static final String RESULTS_FOLDER = "results/";
    private static final int TEST_RUNS = 30;

    public static void main(String[] args) throws CloneNotSupportedException, IOException {
        List<Problem> problems = readBinPackingProblems(PROBLEM_FILEPATH);
        Map<String, Algorithm> algorithms = new HashMap<>();
        Map<String, Map<String, List<Result>>> results = new HashMap<>(); // Stores averages for each problem-algorithm pair
        algorithms.put("GA", new GeneticAlgorithm());
        algorithms.put("FA", new FireFlyAlgorithm());
        algorithms.put("TS", new TabuSearchAlgorithm());
        algorithms.put("MFF", new ModifiedFirstFitDecreasingAlgorithm());
        // TODO: Add other algorithms here

        System.out.println("Bin Packing Problem Solver");
        System.out.printf("Found %d problems in %s%n", problems.size(), PROBLEM_FILEPATH);
        System.out.printf("Using %d algorithm(s):%n", algorithms.size());

        int index = 1;
        for (Map.Entry<String, Algorithm> entry : algorithms.entrySet()) {
            System.out.printf("%d. %s%n", index++, entry.getKey());
        }

        System.out.println();
        for (Problem problem : problems) {
            for (Map.Entry<String,
                    Algorithm> entry : algorithms.entrySet()) {
                String algorithmName = entry.getKey();
                Algorithm algorithm = entry.getValue();

                if (!results.containsKey(problem.getName())) {
                    results.put(problem.getName(), new HashMap<>());
                }
                if (!results.get(problem.getName()).containsKey(algorithmName)) {
                    results.get(problem.getName()).put(algorithmName, new ArrayList<>());
                }

                for (int i = 0; i < TEST_RUNS; i++) {
                    Solution solution = algorithm.solve(problem);
                    ArrayList<ItemFactory> bins = solution.bins.getBins();
                    Result result = solution.evaluateResult(problem.getName(), algorithmName);
                    //result.printOut();
                    //result.printIterationData();

                    // Add the result to the list for averaging later
                    results.get(problem.getName()).get(algorithmName).add(result);
                }
            }
            // TODO: remove this break statement to run all problems
            //break;
        }

        printAveragesToCSV(results); // for avg. number of bins and run time bar chart
        printDetailedIterationDataToCSV(results); // for convergence graph
        printRuntimeDataToCSV(results); // for runtime box plot

        double totalRunTime = results.values().stream()
                .flatMap(m -> m.values().stream())
                .flatMap(List::stream)
                .mapToLong(Result::getRuntime)
                .sum();
        System.out.printf("Total execution time: %.3fms%n", totalRunTime);
        System.out.println("Results saved to folder: " + RESULTS_FOLDER);
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
                    if (problem != null) {
                        if (problem.getNumberOfData() == 0) {
                            problem.setNumberOfData(Integer.parseInt(line.trim()));
                        } else if (problem.getCapacity() == 0) {
                            problem.setCapacity(Integer.parseInt(line.trim()));
                        } else {
                            String[] parts = line.trim().split("\\s+");
                            int weight = Integer.parseInt(parts[0]);
                            int count = Integer.parseInt(parts[1]);
                            problem.items.addItem(weight, count);
                            problem.setNumberOfItems(problem.getNumberOfItems() + count);
                        }
                    }
                }
            }

            // Add the last problem
            if (problem != null) {
                problems.add(problem);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return problems;
    }

    public static void printAveragesToCSV(Map<String, Map<String, List<Result>>> results) throws IOException {
        FileWriter csvWriter = new FileWriter(RESULTS_FOLDER + "summary.csv");
        String header = String.format("Problem,Algorithm,Avg. Number of Bins,Avg. Bin Fullness, Avg. Fairness of Packing, Avg. Execution Time (ms),Total Execution Time (ms)%n");
        csvWriter.write(header);

        String previousProblemName = "";
        for (Map.Entry<String, Map<String, List<Result>>> problemResults : results.entrySet()) {
            String problemName = problemResults.getKey();
            for (Map.Entry<String, List<Result>> algorithmResults : problemResults.getValue().entrySet()) {
                String algorithmName = algorithmResults.getKey();
                List<Result> resultsList = algorithmResults.getValue();

                double averageNumberOfBins = resultsList.stream().mapToDouble(Result::getNumberOfBins).average().orElse(0.0);
                double averageBinFullness = resultsList.stream().mapToDouble(Result::getBinFullness).average().orElse(0.0);
                double averageFairnessOfPacking = resultsList.stream().mapToDouble(Result::getFairnessOfPacking).average().orElse(0.0);
                double averageRunTime = resultsList.stream().mapToDouble(Result::getRuntime).average().orElse(0.0);
                double totalRunTime = resultsList.stream().mapToLong(Result::getRuntime).sum();
                if (previousProblemName.equals(problemName)) {
                    problemName = "";
                } else {
                    previousProblemName = problemResults.getKey();
                }

                String output = String.format("%s,%s,%.3f,%.3f,%.3f,%.3f,%.3f%n",
                        problemName, algorithmName, averageNumberOfBins, averageBinFullness, averageFairnessOfPacking, averageRunTime, totalRunTime);
                csvWriter.write(output);
            }
        }

        csvWriter.flush();
        csvWriter.close();
    }

    private static void printDetailedIterationDataToCSV(Map<String, Map<String, List<Result>>> results) throws IOException {
        FileWriter csvWriter = new FileWriter(RESULTS_FOLDER + "iterations.csv");

        // Find the problem with the highest number of items
        String selectedProblem = null;
        int maxItems = Integer.MIN_VALUE;
        for (Map.Entry<String, Map<String, List<Result>>> entry : results.entrySet()) {
            int totalItems = entry.getValue().values().stream()
                    .flatMap(List::stream)
                    .mapToInt(Result::getNumberOfItems)
                    .sum();
            if (totalItems > maxItems) {
                maxItems = totalItems;
                selectedProblem = entry.getKey();
            }
        }

        if (selectedProblem == null) {
            // No problem found, exit
            return;
        }

        // Print problem name
        System.out.println("Selected problem: " + selectedProblem + " with " + maxItems + " items");
        // Collect unique algorithms
        Set<String> algorithms = new HashSet<>();
        algorithms.addAll(results.get(selectedProblem).keySet());

        // Write header
        StringBuilder header = new StringBuilder("Iteration");
        for (String algorithm : algorithms) {
            header.append(",").append(algorithm);
        }
        header.append("\n");
        csvWriter.write(header.toString());

        // Collect iteration data for selected problem and each algorithm
        Map<Integer, Map<String, Integer>> iterationDataMap = new HashMap<>();
        Map<String, List<Result>> selectedProblemResults = results.get(selectedProblem);
        for (Map.Entry<String, List<Result>> algorithmResults : selectedProblemResults.entrySet()) {
            String algorithmName = algorithmResults.getKey();
            List<Result> resultsList = algorithmResults.getValue();
            for (Result result : resultsList) {
                HashMap<Integer, Integer> iterationData = result.getIterationData();
                for (Map.Entry<Integer, Integer> entry : iterationData.entrySet()) {
                    int iteration = entry.getKey();
                    int numberOfBins = entry.getValue();
                    iterationDataMap.computeIfAbsent(iteration, k -> new HashMap<>())
                            .put(algorithmName, numberOfBins);
                }
            }
        }

        // Write iteration data to CSV
        for (Map.Entry<Integer, Map<String, Integer>> entry : iterationDataMap.entrySet()) {
            int iteration = entry.getKey();
            Map<String, Integer> algorithmData = entry.getValue();
            StringBuilder output = new StringBuilder(iteration + "");
            for (String algorithm : algorithms) {
                output.append(",").append(algorithmData.getOrDefault(algorithm, 0));
            }
            output.append("\n");
            csvWriter.write(output.toString());
        }

        csvWriter.flush();
        csvWriter.close();
    }


//    private static void printDetailedIterationDataToCSV(Map<String, Map<String, List<Result>>> results) throws IOException {
//        FileWriter csvWriter = new FileWriter(RESULTS_FOLDER + "iterations.csv");
//        String header = String.format("Problem,Algorithm,Iteration,Number of Bins%n");
//        csvWriter.write(header);
//
//        for (Map.Entry<String, Map<String, List<Result>>> problemResults : results.entrySet()) {
//            String problemName = problemResults.getKey();
//            for (Map.Entry<String, List<Result>> algorithmResults : problemResults.getValue().entrySet()) {
//                String algorithmName = algorithmResults.getKey();
//                List<Result> resultsList = algorithmResults.getValue();
//                for (Result result : resultsList) {
//                    HashMap<Integer, Integer> iterationData = result.getIterationData();
//                    for (Map.Entry<Integer, Integer> entry : iterationData.entrySet()) {
//                        String output = String.format("%s,%s,%d,%d%n",
//                                problemName, algorithmName, entry.getKey(), entry.getValue());
//                        csvWriter.write(output);
//                    }
//                }
//            }
//        }
//
//        csvWriter.flush();
//        csvWriter.close();
//    }
//    private static void printDetailedIterationDataToCSV(Map<String, Map<String, List<Result>>> results) throws IOException {
//        FileWriter csvWriter = new FileWriter(RESULTS_FOLDER + "iterations.csv");
//        String header = String.format("Problem,Algorithm,Iteration,Number of Bins%n");
//        csvWriter.write(header);
//
//        String previousProblemName = "";
//        for (Map.Entry<String, Map<String, List<Result>>> problemResults : results.entrySet()) {
//            String problemName = problemResults.getKey();
//            for (Map.Entry<String, List<Result>> algorithmResults : problemResults.getValue().entrySet()) {
//                String algorithmName = algorithmResults.getKey();
//                List<Result> resultsList = algorithmResults.getValue();
//                for (Result result : resultsList) {
//                    HashMap<Integer, Integer> iterationData = result.getIterationData();
//                    for (Map.Entry<Integer, Integer> entry : iterationData.entrySet()) {
//                        if (previousProblemName.equals(problemName)) {
//                            problemName = "";
//                        } else {
//                            previousProblemName = problemName;
//                        }
//                        String output = String.format("%s,%s,%d,%d%n",
//                                problemName, algorithmName, entry.getKey(), entry.getValue());
//                        csvWriter.write(output);
//                    }
//                }
//            }
//        }
//
//        csvWriter.flush();
//        csvWriter.close();
//    }

    private static void printRuntimeDataToCSV(Map<String, Map<String, List<Result>>> results) {
        try {
            FileWriter csvWriter = new FileWriter(RESULTS_FOLDER + "runtimes.csv");
            String header = String.format("Problem,Algorithm,Runtime (ms)%n");
            csvWriter.write(header);

            String previousProblemName = "";
            for (Map.Entry<String, Map<String, List<Result>>> problemResults : results.entrySet()) {
                String problemName = problemResults.getKey();
                for (Map.Entry<String, List<Result>> algorithmResults : problemResults.getValue().entrySet()) {
                    String algorithmName = algorithmResults.getKey();
                    List<Result> resultsList = algorithmResults.getValue();
                    for (Result result : resultsList) {
                        if (previousProblemName.equals(problemName)) {
                            problemName = "";
                        } else {
                            previousProblemName = problemName;
                        }
                        String output = String.format("%s,%s,%d%n", problemName, algorithmName, result.getRuntime());
                        csvWriter.write(output);
                    }
                }
            }

            csvWriter.flush();
            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
