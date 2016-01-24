package hmo.projekt.GeneticAlgorithm;

import hmo.projekt.Instance;
import hmo.projekt.PopulationGenerator;
import hmo.projekt.PrettyPrint;
import hmo.projekt.structures.instance.Worker;
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
    private final int POPULATION_SIZE = 100;
    // koliki postotak ukupne populacije će se križati, ostatak će biti odbačen
    private final double SURVIVALE_RATE = 0.1;
    private final int OPTIMAL_SOLUTION  = 0;
    // maksimalan broj iteracija bez poboljšanja
    private final int MAX_ITERATIONS = 300;
    
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
        this.population = this.population.subList(0 ,(int)(this.NUMBER_OF_PARENTS) );
        Collections.sort(this.population);

//        System.out.println("Total result: " + this.population.get(0).fitness + 
//                " Staff result: " + this.population.get(0).staffFitness +
//                " Shift result: " + this.population.get(0).shiftFitness
//        );
//        PrettyPrint.scheduleToFile(population.get(0), instance);
        
        this.start();
    }
    
    public void start () {
        
        int parent_1, parent_2;
        
        while (!this.isSatisfying(population)){
            parent_1 = 0;
            parent_2 = 0;
            for(int i = this.NUMBER_OF_PARENTS; i < this.POPULATION_SIZE -1 ; i++){
                
                while (parent_1 == parent_2) {
                    parent_1 = (int) (Math.random()*this.NUMBER_OF_PARENTS);
                    parent_2 = (int) (Math.random()*this.NUMBER_OF_PARENTS);
                }
                
                StaffSchedule offspring = new StaffSchedule(this.instance);
                
                this.crossover.apply(population.get(parent_1), population.get(parent_2), this.instance, offspring);
                this.mutate.apply(offspring, generator, this.instance); 
                offspring.calculateShiftCover(this.instance.shiftCover);
                this.corrections.balanceDayShifts(offspring, instance);
                offspring.calculateFitness(this.instance);
                population.add(offspring);
            }
            Collections.sort(population);
            population = population.subList(0, this.NUMBER_OF_PARENTS);
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
    
    public static boolean isFeasible (int[] schedule, Instance instance, int workerId) {
	// provjera jedne smjene dnevno nije potrebna, jer struktura ne dozvoljava vise od jedne	

		// trenutni radnik
			Worker worker = instance.staff.get(workerId);
		
		// broj radnih vikenda za trenutnog radnika
			int workingWeekends = 0;
			
		// pronadi ukupno vrijeme rada za tog radnika
			int totalMinutes = 0;
		
		// broj uzastopnih smjena
			int consecutiveShifts = 0;
		
		// broj uzastopnih dana odmora
			int consecutiveDaysOff = 0;
		
		// iteriraj po workerSchedules.schedule (po danima), svaka iteracija je jedan dan
		// odnosno smjena koju je radnik odradio taj dan
			for (int day = 0; day < schedule.length; day ++) {
				int currentShift = schedule[day]; // trenutna smjena
			
			// ako nije rijec o zadnjem danu, provjeri da li je slijedeca smjena dozvoljena
				if (day != (schedule.length - 1))
				{
					int followingShift = schedule[day+1]; // slijedeca smjena (iduci dan)
					
					// ako radnik nije radio taj ili slijedeci dan, nije potrebna provjera
						if (currentShift != -1 && followingShift != -1)
						{							
						// provjeri da li se slijedeca smjena nalazi u listi smjena koje ne smiju slijediti trenutnoj smjeni
							if (instance.shifts.get(currentShift).cantFollowShift != null && instance.shifts.get(currentShift).cantFollowShift.contains(followingShift))
								return false;
						}
				}
				
			// radnik nije radio taj dan
				if(currentShift == -1) {
				// Pribroji uzastopnim danima odmora
					consecutiveDaysOff++;
					
				// ako se dogodio prijelaz iz smjene u odmor
					if (consecutiveShifts > 0)
					{
					// provjeri je li imao broj smjena manji od minimalnog ili veci od maksimalnog
						if (consecutiveShifts < worker.minConsecutiveShifts || consecutiveShifts > worker.maxConsecutiveShifts)
							return false;
						
					// ponisti trenutni broj uzastopnih smjena
						consecutiveShifts = 0;
					}	
				}
				
			// radnik je radio taj dan
				else {			
				// ako je taj dan radio neku smjenu, pribroji njeno trajanje
					totalMinutes += instance.shifts.get(currentShift).lengthMinutes;
					
				// provjeri je li ukupno vrijeme premasilo maksimum
					if (totalMinutes > worker.maxTotalMinutes)
						return false;

				// provjeri da li se trenutna smjena nalazi u listi smjena koje taj radnik smije raditi
					if (!worker.canWorkShift.contains(currentShift))
						return false;

				// provjeri da li se trenutni dan nalazi u listi slobodnih dana
					if(worker.daysOff.contains(day))
						return false;

				// provjeri je li ovaj dan vikend
					if (instance.weekendShiftsSaturday.contains(day) || instance.weekendShiftsSunday.contains(day)) {
						workingWeekends++;

					// provjeri je li radnik premasio maksimalni broj radnih vikenda
						if (workingWeekends > worker.maxWeekends)
							return false;
					}
					
				// pribroji uzastopnim smjenama
					consecutiveShifts++;
					
				// ako se dogodio prijelaz iz odmora u smjenu
					if (consecutiveDaysOff > 0)
					{
					// provjeri je li imao dovoljno dana odmora, ako nije, rjesenje nije feasible
						if (consecutiveDaysOff < worker.minConsecutiveDaysOff)
							return false;
						
					// ponisti trenutni broj uzastopnih dana odmora
						consecutiveDaysOff = 0;
					}
				}
			}
			
		// provjeri je li ukupno vrijeme manje od minimalnog vremena
			if (totalMinutes < worker.minTotalMinutes)
				return false;
			
		// zadnji slijed uzastopnih smjena/dana odmora je ostao neprovjeren
			if (consecutiveShifts > 0 && (consecutiveShifts < worker.minConsecutiveShifts || consecutiveShifts > worker.maxConsecutiveShifts))
				return false;

			if (consecutiveDaysOff > 0 && (consecutiveDaysOff < worker.minConsecutiveDaysOff))
				return false;
		
        return true;
    }
}
