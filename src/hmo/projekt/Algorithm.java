package hmo.projekt;

import hmo.projekt.structures.schedule.StaffSchedule;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Marko
 */

public class Algorithm {
    
    private final int POPULATION_SIZE = 50;
    private final int OPTIMAL_SOLUTION  = 0;
    private final int MAX_ITERATIONS = 200;
    
    private int bestFitness;
    private int iteration;
    
    private final Instance instance;
    public PopulationGenerator generator;
    
    public Crossover crossover;
    
    public List<StaffSchedule> population ;
    
    public Algorithm(Instance instance) {
        
        this.bestFitness = 10000000 ;
        this.population = new LinkedList<>();
        this.instance = instance;
        this.generator = new PopulationGenerator(this.instance);
        this.crossover = new Crossover();
        
        System.out.println("Generiram inicijalnu populaciju");
            
        for(int i = 0; i< this.POPULATION_SIZE;i++){
            this.population.add( this.generator.generateStaffSchedule() );
        }
        this.population = this.population.subList(0 ,this.POPULATION_SIZE );
        Collections.sort(this.population);

        this.start();
    }
    
    public void start () {
        
        while (!this.isSatisfying(population)){
            for(int i = 0; i < this.POPULATION_SIZE -1 ; i++){
                StaffSchedule offspring = crossover.apply(population.get(i), population.get(i + 1), instance);
             //   offspring = mutate.apply(offspring);          
                offspring.calculateFitness(this.instance.weightForShiftCoverUnder, this.instance.weightForShiftCoverOver);

                population.add(offspring);
            }
            Collections.sort(population);
            population = population.subList(0, this.POPULATION_SIZE);
        }
    }
    
    private void dumpPopulationFitness() {
        for(int index = 0;index < this.POPULATION_SIZE;index ++) {
            System.out.print(" [" + this.population.get(index).totalFitness + "]");
        }
        System.out.println();
    }
    
    private boolean isSatisfying(List<StaffSchedule> population) {
        if (bestFitness > this.population.get(0).totalFitness) {
            PrettyPrint.scheduleToFile(population.get(0), instance);
            bestFitness = this.population.get(0).totalFitness;
            this.iteration =  this.MAX_ITERATIONS;
        } else {
            -- this.iteration;
        }
        System.out.println("Current best result: "+ bestFitness + " iteration " + this.iteration);
    //    this.dumpPopulationFitness();
        return (bestFitness <= OPTIMAL_SOLUTION || this.iteration == 0 ) ;
    }
}
