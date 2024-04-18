package algorithms;

import factories.ItemFactory;
import objects.Problem;
import objects.Solution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class ParticleSwarmOptimization implements Algorithm{
	private int populationSize;
	private List<Integer> items;
	private double inertia;
	private double acceleration_Coefficient1;
	private double acceleration_Coefficient2;
	private int binCapacity;
	
	public ParticleSwarmOptimization() {
		//Initializing Custom Parameters
		this.populationSize = 100;
		this.inertia = 1.2;
		this.acceleration_Coefficient1 = 4;
		this.acceleration_Coefficient2 = 0.5;
		items = new ArrayList<Integer>();
	}
	
	@Override
	public Solution solve(Problem problem, int timeLimit) {
        Solution solution = new Solution(problem); // This should be at the top, it initializes the runtime timer
        this.binCapacity = problem.getCapacity();
        HashMap<Integer, Integer> problemItems = problem.items.getItems();
        for (int weight : problemItems.keySet()) { // convert hashmap into array of integers
        	for(int i = 0; i < problemItems.get(weight);i++) {
        		items.add(weight);
        	}
        }
        
        int Particle_Position = items.size();
        List<Particle> population = new ArrayList<>();
        for (int i = 0; i<populationSize; i++) {
        	population.add(new Particle(createIndividual(items, Particle_Position),binCapacity));
        }
        
        int iteration=0;
        while (solution.getCurrentRuntime() < timeLimit * 1000L) {
        	Collections.sort(population, (c1, c2) -> Integer.compare(c1.getFitness(), c2.getFitness()));
        	population = moveParticles(population);
        	iteration++;
        }
        
        System.out.println("Iteration : " + iteration);
 
        Particle fittestParticle= Collections.min(population, Comparator.comparing(Particle::getFitness));
        int currentBinCapacity = 0;
        ItemFactory items = new ItemFactory();
        for (int particle : fittestParticle.getCurrentPosition()) {
        	if (currentBinCapacity + particle > binCapacity) {// bin full
        		solution.bins.createBin(items); //add to list of bins
        		items = new ItemFactory(); //create new bin
        		currentBinCapacity = 0;
        	}
        	items.addItem(particle, 1); //add item to bin
        	currentBinCapacity += particle;
        }
        
        solution.bins.createBin(items); //add final bin to list of bins
        
        
        
        return solution.finalizeResult();
    }
	
    private List<Integer> createIndividual(List<Integer> items, int Particle_Position) {
        List<Integer> individual = new ArrayList<>(items);
        Collections.shuffle(individual);
        return individual;
    }
	
	private List<Particle> moveParticles(List<Particle> Particles){
        List<Particle> nextPosition = new ArrayList<>();
        Particle Global_BestParticle = Particles.get(0);
        
        Random random = new Random();
        for(Particle particle: Particles) {
        	List<Integer> Current_Positions = particle.getCurrentPosition();
        	List<Integer> New_Position = new ArrayList<>(Current_Positions);
        	List<Integer> Velocities = new ArrayList<>();
        	List<Integer> newPosition = new ArrayList<>();
        	
        	for(int i = 0; i<Current_Positions.size();i++) {
        		double R1 = random.nextDouble();
        		double R2 = random.nextDouble();
        		int personalBest_Solution = particle.getPersonalBestPosition().get(i);
        		int globalBest_Solution = Global_BestParticle.getCurrentPosition().get(i);
        		int CurrentPosition = Current_Positions.get(i);
        		
        		int Velocity = (int)(inertia*CurrentPosition+
        							 acceleration_Coefficient1*R1*(personalBest_Solution - CurrentPosition)+
        							 acceleration_Coefficient2*R2*(globalBest_Solution - CurrentPosition));
        		Velocities.add(Velocity);
        	}
        	
            for (int i = 0; i < Current_Positions.size(); i++) {
                int newPos = Current_Positions.get(i) + Velocities.get(i);
                newPosition.add(newPos);
            }
            
        	nextPosition.add(new Particle(New_Position, binCapacity));}
        	return nextPosition;
        }
}

class Particle{
    private List<Integer> Current_Position;
    private List<Integer> PersonalBest_Position;
    private int fitness;
    
    public Particle(List<Integer> Current_Position, int binCapacity) {
    	this.Current_Position = Current_Position;
    	this.PersonalBest_Position = new ArrayList<>(Current_Position);
    	calculateFitness(binCapacity);
    }
    
    public List<Integer> getCurrentPosition() {
        return Current_Position;
    }
    
    public List<Integer> getPersonalBestPosition(){
    	return PersonalBest_Position;
    }

    public int getFitness() {
        return fitness;
    }
    
    void calculateFitness(int binCapacity) {
        fitness = 0;
        int currentBinCapacity = 0;
        for (int Position : Current_Position) {
            if (currentBinCapacity + Position > binCapacity) {
                fitness++;
                currentBinCapacity = 0;
            }
            currentBinCapacity += Position;
        }
    }
    
}
