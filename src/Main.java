import java.io.*;
import java.util.ArrayList;
import java.util.List;

import objects.Result;

import algorithms.Algorithm;
import algorithms.GeneticAlgorithm;
//import algorithms.ParticleSwarmOptimization;
//import algorithms.FireFlyAlgorithm;
import factories.ItemFactory;
import objects.Problem;
import objects.Solution;

public class Main {
    public static void main(String[] args) {
        String filename = "BPP.txt";
        List<Problem> problems = readBinPackingProblems(filename);
        for (Problem problem : problems) {
            // Prin problems
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

            // Other algorithms goes here


            // Put this at the end for spacing new line
            System.out.println();
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
}