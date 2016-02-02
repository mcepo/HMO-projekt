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
    
    
    // ukupana veličina populacije sa potomcima 
    private final int POPULATION_SIZE = 500;
    // koliki postotak ukupne populacije će se križati, ostatak će biti odbačen
    private final double SURVIVALE_RATE = 0.90;
    private final int OPTIMAL_SOLUTION  = 0;
    // maksimalan broj iteracija bez poboljšanja
    private final int MAX_ITERATIONS = 10000;
    
    private final int NUMBER_OF_PARENTS;
    
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
        this.NUMBER_OF_PARENTS = (int)(this.POPULATION_SIZE * this.SURVIVALE_RATE);
        
        System.out.println("Generiram inicijalnu populaciju");
            
        for(int i = 0; i< this.POPULATION_SIZE;i++){
            this.population.add( this.generator.generateStaffSchedule() );
        }
        this.population.subList(this.NUMBER_OF_PARENTS, this.POPULATION_SIZE);
        Collections.sort(this.population);
    }
    
    public void solve () {
        
        int parent_1, parent_2;
        double totalFitness;
        double currFitness_1;
        double currFitness_2;
        int index;
        
        while (!this.isSatisfying(population)){
            totalFitness = 0;
            for (int i=0;i< population.size();i++){
               
                totalFitness += (double) ((double) population.get(0).fitness/(double)population.get(i).fitness );
            }
       //     System.out.println(totalFitness);
            
            for(int i = this.NUMBER_OF_PARENTS; i < this.POPULATION_SIZE ; i++){
                
                parent_1 = -1;
                parent_2 = -1;
                
                while (parent_1 == parent_2 || parent_1 == -1 || parent_2 == -1) {

                    currFitness_1 = (Math.random()*totalFitness);
                    currFitness_2 = (Math.random()*totalFitness);
                    
                    parent_1 = -1;
                    parent_2 = -1;
                    
                    for(int j=0;j<population.size() && (parent_1 == -1 || parent_2 == -1);j++){
                        
                        currFitness_1 -= (double)((double)population.get(0).fitness/(double)population.get(i).fitness );
                        currFitness_2 -= (double)((double)population.get(0).fitness/(double)population.get(i).fitness );
                        
                        if (currFitness_1 <= 0 && parent_1 == -1) {
                            parent_1 = j;
                  //          System.out.println("parent_1 "  + parent_1);
                        }
                        
                        if (currFitness_2 <= 0 && parent_2 == -1) {
                            parent_2 = j;
                   //         System.out.println("parent_2 "  + parent_2);
                        }
                    }
                }
                
          //      System.out.println("Roditelji " + parent_1 + " " + parent_2);
                
                StaffSchedule offspring = new StaffSchedule(this.instance);
                
                this.crossover.apply(population.get(parent_1), population.get(parent_2), this.instance, offspring);
                this.mutate.apply(offspring, generator, this.instance); 
                this.corrections.apply(offspring, instance);
                population.add(offspring);
                
            }
            Collections.sort(population);
            population.subList(this.NUMBER_OF_PARENTS, this.POPULATION_SIZE).clear();
        }
                    
            System.out.println(" ******* Zapisujem rasporede u datoteke *******");
            for(index = 1; index < 11 ; index ++) {
                PrettyPrint.scheduleToFile(population.get(index-1), instance, "res-"+index+"-cepo.txt");
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
