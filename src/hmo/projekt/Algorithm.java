package hmo.projekt;

import hmo.projekt.structures.schedule.Schedule;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Marko
 */

public class Algorithm {
    
    private final int INIT_POPULATION = 10;
    
    
    private final Instance instance;
    public PopulationGenerator generator;
    
    
    public List<Schedule> population ;
    
    public Algorithm(Instance instance) {
        
        this.population = new LinkedList<Schedule>();
        
        this.instance = instance;
        this.generator = new PopulationGenerator(this.instance);
        
        System.out.println("Generiram inicijalnu populaciju");
        
        for(int i = 0; i< this.INIT_POPULATION;i++){
            System.out.println("GENERIRAM RASPORED " + i);
            this.population.add(this.generator.generateSchedule());
        }
        PrettyPrint.schedule(population.get(0), this.instance);
    }
}
