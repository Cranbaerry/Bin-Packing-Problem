package objects;

import factories.ItemFactory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Result {
    private String algorithmName, problemName;
    private int numberOfBins;
    private long runtime;
    private double binFullness;
    private double fairnessOfPacking;

    public Result() {
        this.algorithmName = "";
        this.numberOfBins = 0;
        this.runtime = 0;
        this.binFullness = 0;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName;
    }

    public String getProblemName() {
        return problemName;
    }

    public void setProblemName(String problemName) {
        this.problemName = problemName;
    }

    public int getNumberOfBins() {
        return numberOfBins;
    }

    public void setNumberOfBins(int numberOfBins) {
        this.numberOfBins = numberOfBins;
    }

    public long getRuntime() {
        return runtime;
    }

    public void setRuntime(long runtime) {
        this.runtime = runtime;
    }

    public double getBinFullness() {
        return binFullness;
    }

    public void setBinFullness(double binFullness) {
        this.binFullness = binFullness;
    }

    public double getFairnessOfPacking() {
        return fairnessOfPacking;
    }

    public void setFairnessOfPacking(double fairnessOfPacking) {
        this.fairnessOfPacking = fairnessOfPacking;
    }

    public void printOut() {
        System.out.println("Result for algorithm: " + algorithmName);
        System.out.println("Number of Bins: " + numberOfBins);
        System.out.println("Runtime: " + runtime + "ms");
        System.out.println("Bin Fullness: " + String.format("%.3f", binFullness) + "%");
        System.out.println("Fairness of Packing: " + String.format("%.3f", fairnessOfPacking));
    }

    public JFrame plotGraph(ArrayList<ItemFactory> bins) {
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

        JFreeChart chart = ChartFactory.createStackedBarChart("Bins for " + this.problemName + " using " + this.algorithmName, "Bin", "Weight", dataset);
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

        String filePath = "results/problem_" + problemName + " " + algorithmName + ".png";
        saveFrameAsImage(frame, filePath);
        return frame;
    }

    // Method to generate random colors
    private Color getRandomColor() {
        return new Color((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256));
    }

    // Method to save the frame as an image file
    private void saveFrameAsImage(JFrame frame, String filePath) {
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
