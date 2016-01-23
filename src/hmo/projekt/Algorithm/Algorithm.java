package hmo.projekt.Algorithm;

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
    
    private final int POPULATION_SIZE = 20;
    private final int OPTIMAL_SOLUTION  = 0;
    private final int MAX_ITERATIONS = 100;
    
    private int bestFitness;
    private int iteration;
    
    private final Instance instance;
    public PopulationGenerator generator;
    
    public List<StaffSchedule> population ;
    
    public Algorithm(Instance instance) {
        
        this.bestFitness = 10000000 ;
        this.population = new LinkedList<>();
        this.instance = instance;
        this.generator = new PopulationGenerator(this.instance);
        
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
                
                StaffSchedule offspring = Crossover.apply(population.get(i), population.get(i + 1), instance);
                Mutate.apply(offspring, generator, instance); 
                offspring.calculateShiftCover(instance.shiftCover);
                Corrections.balanceDayShifts(offspring, instance);
                offspring.calculateFitness(this.instance.weightForShiftCoverUnder, this.instance.weightForShiftCoverOver);
                population.add(offspring);
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
}
