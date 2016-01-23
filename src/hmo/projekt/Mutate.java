package hmo.projekt;

import hmo.projekt.structures.schedule.StaffSchedule;
import hmo.projekt.structures.schedule.WorkerSchedule;

/**
 *
 * @author Marko
 */
public class Mutate {
    
    private static final double PROBABILITY = 0.1;
    
    public static void apply (StaffSchedule staffSchedule, PopulationGenerator generator, Instance instance) {
        
        WorkerSchedule workerSchedule;
        
        for(int workerId = 0 ; workerId < staffSchedule.workerSchedules.length; workerId ++) {
            if (Math.random() < Mutate.PROBABILITY) {
                while ((workerSchedule = generator.generateWorkerSchedule(workerId)) == null ) { }
                workerSchedule.calculateFitness(instance.staff.get(workerId), instance.numberOfDays);
                staffSchedule.workerSchedules[workerId] = workerSchedule;
            }
        }
    }
}
