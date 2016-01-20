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
    
    private final int INIT_POPULATION = 50;
    private final float PASS_RATE = (int) 0.6;
    
    private final Instance instance;
    public PopulationGenerator generator;
    
    public List<StaffSchedule> population ;
    
    public Algorithm(Instance instance) {
        
        this.population = new LinkedList<>();
        this.instance = instance;
        this.generator = new PopulationGenerator(this.instance);
        
        System.out.println("Generiram inicijalnu populaciju");
        
        int bestFitness = 1000000;
        do {
            
            if (!this.population.isEmpty()) {
                bestFitness = this.population.get(0).totalFitness;
            }
            
            for(int i = this.population.size(); i< this.INIT_POPULATION;i++){

                this.population.add(this.generator.generateStaffSchedule());

            }
            this.population.subList((int)(this.population.size() * this.PASS_RATE), this.population.size() -1 );
            Collections.sort(this.population, new CustomComparator());
        } while (bestFitness > this.population.get(0).totalFitness);
        System.out.println("Best fitness " + bestFitness);
        PrettyPrint.scheduleToFile(this.population.get(0), this.instance);
    }
}
