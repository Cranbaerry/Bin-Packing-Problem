import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import algorithms.Algorithm;
import algorithms.GeneticAlgorithm;
import algorithms.ParticleSwarmOptimization;
import factories.ItemFactory;
import objects.Problem;
import objects.Solution;


public class Main {
    public static void main(String[] args) {
        String filename = "BPP.txt";
        List<Problem> problems = readBinPackingProblems(filename);
        for (Problem problem : problems) {
            System.out.println("Problem Name: " + problem.getName());
            System.out.println("Number of Items: " + problem.getNumberOfItems());
            System.out.println("Bin Capacity: " + problem.getCapacity());
            // System.out.println("Items:");
            // HashMap<Integer, Integer> problemItems = problem.items.getItems();
            // for (int weight : problemItems.keySet()) {
            //    System.out.println("Weight: " + weight + ", Quantity: " + problemItems.get(weight));
            // }
            System.out.println();

            // Solve the problem with a genetic algorithm
            Algorithm geneticAlgorithm = new GeneticAlgorithm();
            Solution solution = geneticAlgorithm.solve(problem, 3); // 5 seconds timeout
            System.out.println("Genetic Algorithm Solution:");
            System.out.println("Runtime: " + solution.getTotalRuntime() + " ms");
            System.out.println("Total bins used: " + solution.bins.getNumberOfBins());
            System.out.println("Bins:");
            ArrayList<ItemFactory> bins = solution.bins.getBins();
//            plotBins(bins, problem.getName(),"Genetic Algorithm");
//            for (int i = 0; i < bins.size(); i++) {
//                System.out.println("Bin " + (i + 1) + ":");
//                ItemFactory items = bins.get(i);
//                HashMap<Integer, Integer> binItems = items.getItems();
//                for (int weight : binItems.keySet()) {
//                    System.out.println("Weight: " + weight + ", Quantity: " + binItems.get(weight));
//                }
//            }
//            System.out.println();
            
            JFrame frame = plotBins(bins, problem.getName(),"Genetic Algorithm");
            String filePath = "results/problem_" + problem.getName() + " Genetic Algorithm"+ ".png";
            saveFrameAsImage(frame, filePath);
            System.out.println("Image saved to: " + filePath);      
            
            Algorithm particleSwarmOptimization = new ParticleSwarmOptimization();
            Solution solution2 = particleSwarmOptimization.solve(problem, 3); // 5 seconds timeout
            System.out.println("Particle Swarm Optimization Solution:");
            System.out.println("Runtime: " + solution2.getTotalRuntime() + " ms");
            System.out.println("Total bins used: " + solution2.bins.getNumberOfBins());
            System.out.println("Bins:");
            ArrayList<ItemFactory> bins2 = solution2.bins.getBins();
//            plotBins(bins, problem.getName(),"Genetic Algorithm");
//            for (int i = 0; i < bins.size(); i++) {
//                System.out.println("Bin " + (i + 1) + ":");
//                ItemFactory items = bins.get(i);
//                HashMap<Integer, Integer> binItems = items.getItems();
//                for (int weight : binItems.keySet()) {
//                    System.out.println("Weight: " + weight + ", Quantity: " + binItems.get(weight));
//                }
//            }
//            System.out.println();
            
            JFrame frame2 = plotBins(bins2, problem.getName(),"PSO");
            String filePaths = "results/problem_" + problem.getName() + " PSO "+ ".png";
            saveFrameAsImage(frame2, filePaths);
            System.out.println("Image saved to: " + filePaths);
//            break;
        }
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
                    problem.setName(line.substring(1).trim());
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
    
    // Plotting method
//    public static void plotBins(ArrayList<ItemFactory> bins,String probName, String algorithm) {
//        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
//        int itemIndex = 1;
//        for (ItemFactory bin : bins) {
//            HashMap<Integer, Integer> binItems = bin.getItems();
//            Iterator<Map.Entry<Integer, Integer>> iterator = binItems.entrySet().iterator();
//            while (iterator.hasNext()) {
//                Map.Entry<Integer, Integer> entry = iterator.next();
//                int weight = entry.getKey();
//                int count = entry.getValue();
//                int totalWeight = weight * count; // Adjusted for total weight
//                dataset.addValue(totalWeight, "Item " + itemIndex, "Bin " + (bins.indexOf(bin) + 1));
//                itemIndex++;
//            }
//        }
//
//        JFreeChart chart = ChartFactory.createStackedBarChart("Bins for " + probName + " using " + algorithm, "Bin", "Weight", dataset);
//        CategoryPlot plot = chart.getCategoryPlot();
//        plot.setRangePannable(true);
//
//        StackedBarRenderer renderer = new StackedBarRenderer();
//        for (int i = 0; i < bins.size(); i++) {
//            renderer.setSeriesPaint(i, getRandomColor());
//        }
//        plot.setRenderer(renderer);
//
//        CategoryAxis xAxis = plot.getDomainAxis();
//        xAxis.setCategoryMargin(0.1);
//
//        chart.getLegend().setVisible(false); // Disable legend
//
//        ChartPanel chartPanel = new ChartPanel(chart);
//        JFrame frame = new JFrame("Bins");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setLayout(new BorderLayout());
//        frame.add(chartPanel, BorderLayout.CENTER);
//        frame.pack();
//        frame.setVisible(true);
//    }

    // Method to generate random colors
//    private static Color getRandomColor() {
//        return new Color((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256));
//    }
    
    public static JFrame plotBins(ArrayList<ItemFactory> bins,String probName, String algorithm) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        int itemIndex = 1;
        for (ItemFactory bin : bins) {
            HashMap<Integer, Integer> binItems = bin.getItems();
            Iterator<Map.Entry<Integer, Integer>> iterator = binItems.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, Integer> entry = iterator.next();
                int weight = entry.getKey();
                int count = entry.getValue();
                int totalWeight = weight * count; // Adjusted for total weight
                dataset.addValue(totalWeight, "Item " + itemIndex, "Bin " + (bins.indexOf(bin) + 1));
                itemIndex++;
            }
        }

        JFreeChart chart = ChartFactory.createStackedBarChart("Bins for " + probName + " using " + algorithm, "Bin", "Weight", dataset);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setRangePannable(true);

        StackedBarRenderer renderer = new StackedBarRenderer();
        for (int i = 0; i < bins.size(); i++) {
            renderer.setSeriesPaint(i, getRandomColor());
        }
        plot.setRenderer(renderer);

        CategoryAxis xAxis = plot.getDomainAxis();
        xAxis.setCategoryMargin(0.1);

        chart.getLegend().setVisible(false); // Disable default legend

        // Custom legend
        // Combine chart panel and legend panel into one frame
        JFrame frame = new JFrame("Bins");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(new ChartPanel(chart), BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        
        return frame;
    }

    // Method to generate random colors
    private static Color getRandomColor() {
        return new Color((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256));
    }

    // Method to save the frame as an image file
    private static void saveFrameAsImage(JFrame frame, String filePath) {
        try {
            File file = new File(filePath);
            File parentDir = file.getParentFile();
            if (!parentDir.exists()) {
                if (!parentDir.mkdirs()) {
                    throw new IOException("Failed to create directory: " + parentDir.getAbsolutePath());
                }
            }

            BufferedImage image = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = image.createGraphics();
            frame.paint(graphics2D);
            ImageIO.write(image, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
}