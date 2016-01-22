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
    
    private final int POPULATION_SIZE = 10;
    private final float PASS_RATE = (float) 0.7;
    private final int OPTIMAL_SOLUTION  = 0;
    private int MAX_ITERATIONS = 30;
    
    private int bestFitness;
    
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
        
        System.out.println("Generiram inicijalnu populaciju (3 - 5 min)");
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
    //    System.out.println(" ******* Best : " + this.population.get(0).totalFitness);
    //    PrettyPrint.scheduleToFile(population.get(0), instance);

        this.start();
    }
    
    public void start () {
        
        while (!this.isSatisfying(population)){
            for(int i = 0; i < this.POPULATION_SIZE -1 ; i++){
                StaffSchedule offspring = crossover.apply(population.get(i), population.get(i + 1),  this.instance.shiftCover);
             //   offspring = mutate.apply(offspring);
                offspring.calculateFitness(this.instance.weightForShiftCoverUnder, this.instance.weightForShiftCoverOver);
                
                this.generator.correctionsInSingleDay(offspring);
                
                population.add(offspring);
            }
            Collections.sort(population);
            population = population.subList(0, this.POPULATION_SIZE);
        }
    //    PrettyPrint.scheduleToStdout(this.population.get(0), this.instance);
//        for(int day = 0; day < 182 ; day ++) {
//            for(int shift = 0;shift < 6; shift ++) {
//                System.out.println(day + "\t" + shift +"\t" + this.population.get(0).shiftCover[day][shift]);
//            }
//        }
    }
    
    private boolean isSatisfying(List<StaffSchedule> population) {
        if (bestFitness > this.population.get(0).totalFitness) {
            PrettyPrint.scheduleToFile(population.get(0), instance);
        }
        bestFitness = this.population.get(0).totalFitness;
        System.out.println("Current best result: "+ bestFitness);
        -- MAX_ITERATIONS;
        return (bestFitness <= OPTIMAL_SOLUTION || MAX_ITERATIONS == 0 ) ;
    }
}
