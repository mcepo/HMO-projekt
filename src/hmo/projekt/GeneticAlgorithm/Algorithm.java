package hmo.projekt.GeneticAlgorithm;

import hmo.projekt.Instance;
import hmo.projekt.PopulationGenerator;
import hmo.projekt.PrettyPrint;
import hmo.projekt.structures.schedule.StaffSchedule;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Marko
 */

public class Algorithm {
    
    private final int POPULATION_SIZE = 30;
    private final int OPTIMAL_SOLUTION  = 0;
    private final int MAX_ITERATIONS = 200;
    
    private int bestFitness;
    private int iteration;
    
    private final Instance instance;
    public PopulationGenerator generator;
    
    public Corrections corrections;
    public Mutate mutate;
    public Crossover crossover;
    
    public List<StaffSchedule> population ;
    
    public Algorithm(Instance instance) {
        
        this.bestFitness = 10000000 ;
        this.population = new LinkedList<>();
        this.instance = instance;
        this.generator = new PopulationGenerator(this.instance);
        this.crossover = new Crossover();
        this.mutate = new Mutate();
        this.corrections = new Corrections();
        
        System.out.println("Generiram inicijalnu populaciju");
            
        for(int i = 0; i< this.POPULATION_SIZE;i++){
            this.population.add( this.generator.generateStaffSchedule() );
        }
        this.population = this.population.subList(0 ,this.POPULATION_SIZE );
        Collections.sort(this.population);

//        System.out.println("Total result: " + this.population.get(0).fitness + 
//                " Staff result: " + this.population.get(0).staffFitness +
//                " Shift result: " + this.population.get(0).shiftFitness
//        );
//        PrettyPrint.scheduleToFile(population.get(0), instance);
        
        this.start();
    }
    
    public void start () {
        
        while (!this.isSatisfying(population)){
            for(int i = 0; i < this.POPULATION_SIZE -1 ; i++){
                
                StaffSchedule offspring = new StaffSchedule(this.instance); 

                
                this.crossover.apply(population.get(i), population.get(i + 1), this.instance, offspring);
                this.mutate.apply(offspring, generator, this.instance); 
                offspring.calculateShiftCover(this.instance.shiftCover);
                this.corrections.balanceDayShifts(offspring, instance);
                offspring.calculateFitness(this.instance);
                if (Algorithm.isFeasible(offspring, instance)) {
                    population.add(offspring);
                }
            }
            Collections.sort(population);
            population = population.subList(0, this.POPULATION_SIZE);
        }
    }
    
    private void dumpPopulationFitness(int count) {
        for(int index = 0;index < count;index ++) {
            System.out.print(" [" + this.population.get(index).fitness + "]");
        }
        System.out.println();
    }
    
    private boolean isSatisfying(List<StaffSchedule> population) {
        if (bestFitness > this.population.get(0).fitness) {
            this.dumpPopulationFitness(10);
            
            System.out.println(" ******* Zapisujem rasporede u datoteke *******");
            for(int index = 1; index < 11 ; index ++) {
                PrettyPrint.scheduleToFile(population.get(index-1), instance, "res-"+index+"-cepo.txt");
            }
            bestFitness = this.population.get(0).fitness;
            this.iteration =  this.MAX_ITERATIONS;
        } else {
            -- this.iteration;
        }
        System.out.println("Current best result: " + bestFitness + 
                " Staff result: " + this.population.get(0).staffFitness +
                " Shift result: " + this.population.get(0).shiftFitness +
                " Iteration: " + this.iteration);
    //    this.dumpPopulationFitness();
        return (bestFitness <= OPTIMAL_SOLUTION || this.iteration == 0 ) ;
    }
    
    public static boolean isFeasible (StaffSchedule staffSchedule, Instance instance) {
        return true;
    }
}
