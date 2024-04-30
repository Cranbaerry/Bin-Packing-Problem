import algorithms.*;
import factories.ItemFactory;
import objects.Problem;
import objects.Result;
import objects.Solution;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static final String PROBLEM_FILEPATH = "BPP.txt";
    public static final String RESULTS_FILEPATH = "results/results.csv";
    public static final int TEST_RUNS = 1;

    public static void main(String[] args) throws CloneNotSupportedException, IOException {
        List<Problem> problems = readBinPackingProblems(PROBLEM_FILEPATH);
        Map<String, Algorithm> algorithms = new HashMap<>();
        Map<String, Map<String, List<Result>>> results = new HashMap<>(); // Stores averages for each problem-algorithm pair
        algorithms.put("Genetic Algorithm", new GeneticAlgorithm());
        algorithms.put("Firefly Algorithm", new FireFlyAlgorithm());
        algorithms.put("Tabu Search Algorithm", new TabuSearchAlgorithm());
        algorithms.put("Modified First Fit Descending", new ModifiedFirstFitDecreasingAlgorithm());
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
                    //result.plotGraph(bins);

                    // Add the result to the list for averaging later
                    results.get(problem.getName()).get(algorithmName).add(result);
                }
            }
            // TODO: remove this break statement to run all problems
            //break;
        }

        generateBarChart("Bin Packing Problem Results", "Problem", "Average Number of Bins", results);
        //generateConvergenceChart("Convergence Chart", "Iteration", "Number of Bins", results);
        printAveragesToCSV(results);
        generateConvChart(results);

        double totalRunTime = results.values().stream()
                .flatMap(m -> m.values().stream())
                .flatMap(List::stream)
                .mapToLong(Result::getRuntime)
                .sum();
        System.out.printf("Total execution time: %.3fms%n", totalRunTime);
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
        FileWriter csvWriter = new FileWriter(RESULTS_FILEPATH);
        String header = String.format("Problem,Algorithm,Avg. Number of Bins,Avg. Bin Fullness, Avg. Fairness of Packing, Avg. Execution Time (ms),Total Execution Time (ms)%n");
        csvWriter.write(header);

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

                String output = String.format("%s,%s,%.3f,%.3f,%.3f,%.3f,%.3f%n",
                        problemName, algorithmName, averageNumberOfBins, averageBinFullness, averageFairnessOfPacking, averageRunTime, totalRunTime);
                csvWriter.write(output);
            }
        }

        csvWriter.flush();
        csvWriter.close();
    }

    public static void generateBarChart(String title, String xLabel, String yLabel, Map<String, Map<String, List<Result>>> results) {
        Map<String, Map<String, Double>> averageData = new HashMap<>();

        for (Map.Entry<String, Map<String, List<Result>>> problemResults : results.entrySet()) {
            String problemName = problemResults.getKey();
            Map<String, List<Result>> algorithmResults = problemResults.getValue();

            Map<String, Double> avgAlgorithmData = new HashMap<>();

            for (Map.Entry<String, List<Result>> algorithmResult : algorithmResults.entrySet()) {
                String algorithmName = algorithmResult.getKey();
                List<Result> resultsList = algorithmResult.getValue();

                double averageNumberOfBins = resultsList.stream().mapToDouble(Result::getNumberOfBins).average().orElse(0.0);
                double averageBinFullness = resultsList.stream().mapToDouble(Result::getBinFullness).average().orElse(0.0);
                double averageFairnessOfPacking = resultsList.stream().mapToDouble(Result::getFairnessOfPacking).average().orElse(0.0);
                double averageRunTime = resultsList.stream().mapToDouble(Result::getRuntime).average().orElse(0.0);

                avgAlgorithmData.put(algorithmName, averageNumberOfBins);
            }

            averageData.put(problemName, avgAlgorithmData);
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Map<String, Double>> entry : averageData.entrySet()) {
            String problemName = entry.getKey();
            Map<String, Double> algorithmData = entry.getValue();

            for (Map.Entry<String, Double> algorithmEntry : algorithmData.entrySet()) {
                String algorithmName = algorithmEntry.getKey();
                Double value = algorithmEntry.getValue();

                dataset.addValue(value, algorithmName, problemName);
            }
        }

        JFreeChart chart = ChartFactory.createBarChart(title, xLabel, yLabel, dataset);
        ApplicationFrame frame = new ApplicationFrame(title);
        frame.setContentPane(new ChartPanel(chart));
        frame.pack();
        frame.setVisible(true);
    }

    public static void generateLineChart(String title, String xLabel, String yLabel, Map<String, Map<String, List<Double>>> data, int iterations) {
        XYDataset dataset = createDataset(data, iterations);
        JFreeChart chart = ChartFactory.createXYLineChart(title, xLabel, yLabel, dataset);

        ApplicationFrame frame = new ApplicationFrame(title);
        frame.setContentPane(new ChartPanel(chart));
        frame.pack();
        frame.setVisible(true);
    }

    private static XYDataset createDataset(Map<String, Map<String, List<Double>>> data, int iterations) {
        DefaultXYDataset dataset = new DefaultXYDataset();

        for (Map.Entry<String, Map<String, List<Double>>> problemData : data.entrySet()) {
            String problemName = problemData.getKey();
            Map<String, List<Double>> algorithmData = problemData.getValue();

            for (Map.Entry<String, List<Double>> algorithmEntry : algorithmData.entrySet()) {
                String algorithmName = algorithmEntry.getKey();
                List<Double> values = algorithmEntry.getValue();

                double[] seriesData = new double[iterations * 2];

                for (int i = 0; i < iterations; i++) {
                    seriesData[i * 2] = i;
                    seriesData[i * 2 + 1] = values.get(i);
                }

                dataset.addSeries(algorithmName + " (" + problemName + ")", new double[][]{seriesData});
            }
        }

        return dataset;
    }

    private static void generateConvChart(Map<String, Map<String, List<Result>>> results) {
        for (Map.Entry<String, Map<String, List<Result>>> problemResults : results.entrySet()) {
            String problemName = problemResults.getKey();
            Map<String, List<Result>> algorithmResults = problemResults.getValue();
            XYSeriesCollection dataset = new XYSeriesCollection();
            for (Map.Entry<String, List<Result>> algorithmResult : algorithmResults.entrySet()) {
                String algorithmName = algorithmResult.getKey();
                List<Result> resultsList = algorithmResult.getValue();
                XYSeries series = new XYSeries(algorithmName);
                // Pick one result from results list with the least number of bins
                Result result = resultsList.stream().min((r1, r2) -> Integer.compare(r1.getNumberOfBins(), r2.getNumberOfBins())).orElse(null);
                if (result == null) { continue; }
                HashMap<Integer, Integer> iterationData = result.getIterationData();
                for (Map.Entry<Integer, Integer> entry : iterationData.entrySet()) {
                    series.add(entry.getKey(), entry.getValue());
                }
                dataset.addSeries(series);
            }

            // Create the chart
            JFreeChart chart = ChartFactory.createXYLineChart(
                    "Convergence Chart for " + problemName,
                    "Iteration",
                    "Value",
                    dataset,
                    PlotOrientation.VERTICAL,
                    true,   // include legend
                    true,
                    false);
            XYPlot plot = chart.getXYPlot();

            plot.setBackgroundPaint(Color.lightGray);
            plot.setForegroundAlpha(0.35f);
            plot.setDomainGridlinePaint(Color.white);
            plot.setRangeGridlinePaint(Color.white);

            XYAreaRenderer renderer = new XYAreaRenderer(XYAreaRenderer.AREA);
            plot.setRenderer(renderer);
            ApplicationFrame frame = new ApplicationFrame("Convergence Chart");
            frame.setContentPane(new ChartPanel(chart));
            frame.pack();
            frame.setVisible(true);
        }
    }
}
