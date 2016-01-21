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
    private final float PASS_RATE = (float) 0.7;
    private final int OPTIMAL_SOLUTION  = 0;
    
    private final Instance instance;
    public PopulationGenerator generator;
    
    public Crossover crossover;
    
    public List<StaffSchedule> population ;
    
    public Algorithm(Instance instance) {
        
        this.population = new LinkedList<>();
        this.instance = instance;
        this.generator = new PopulationGenerator(this.instance);
        this.crossover = new Crossover();
        
        System.out.println("Generiram inicijalnu populaciju");
        
        int bestFitness = 1000000;
        do {
            
            if (!this.population.isEmpty()) {
                bestFitness = this.population.get(0).totalFitness;
            }
            
            for(int i = this.population.size(); i< this.POPULATION_SIZE;i++){

                this.population.add(this.generator.generateStaffSchedule());

            }
            this.population = this.population.subList(0 ,this.POPULATION_SIZE );
            Collections.sort(this.population);
        } while (bestFitness > this.population.get(0).totalFitness);
        
        this.start();
    }
    
    public void start () {
        
        while (!this.isSatisfying(population)){
            for(int i = 0; i < this.POPULATION_SIZE -1 ; i++){
                StaffSchedule offspring = crossover.apply(population.get(i), population.get(i + 1),  this.instance.shiftCover);
             //   offspring = mutate.apply(offspring);
                offspring.calculateFitness(this.instance.weightForShiftCoverUnder, this.instance.weightForShiftCoverOver);
                population.add(offspring);
            }
            Collections.sort(population);
            population = population.subList(0, this.POPULATION_SIZE);
            PrettyPrint.scheduleToFile(population.get(0), instance);
        }
    //    PrettyPrint.scheduleToStdout(this.population.get(0), this.instance);
    }
    
    private boolean isSatisfying(List<StaffSchedule> population) {
        int best = this.population.get(0).totalFitness;
        System.out.println("Current best result: "+ best);
        return best <= OPTIMAL_SOLUTION;
    }
}
