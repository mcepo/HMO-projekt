package hmo.projekt.GeneticAlgorithm;

import hmo.projekt.Instance;
import hmo.projekt.PopulationGenerator;
import hmo.projekt.structures.schedule.StaffSchedule;
import hmo.projekt.structures.schedule.WorkerSchedule;

/**
 *
 * @author Marko
 */
public class Mutate {
    
    private static final double PROBABILITY = 0.10;
    
    public void apply (StaffSchedule staffSchedule, PopulationGenerator generator, Instance instance) {
        
        WorkerSchedule workerSchedule;
        
        for(int workerId = 0 ; workerId < staffSchedule.workerSchedules.length; workerId ++) {
            if (Math.random() < Mutate.PROBABILITY*instance.rouletteWheelForMutate.get(workerId)) {
                while ((workerSchedule = generator.generateWorkerSchedule(workerId)) == null ) { }
                workerSchedule.calculateFitness(instance.staff.get(workerId), instance.numberOfDays);
                staffSchedule.workerSchedules[workerId] = workerSchedule;
            }
        }
    }
}
