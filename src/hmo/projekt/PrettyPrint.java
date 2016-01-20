package hmo.projekt;

import hmo.projekt.structures.schedule.Schedule;
import hmo.projekt.structures.schedule.WorkerSchedule;

/**
 *
 * @author Marko
 */
public class PrettyPrint {
    
    public static void schedule(Schedule schedule, Instance instance) {
        
        for(WorkerSchedule workerSchedule : schedule.workerSchedules) {
            
            for(int day=0; day < instance.numberOfDays; day++ ) {
                if (workerSchedule.schedule.containsKey(day)){
                    System.out.print(instance.shifts.get(workerSchedule.schedule.get(day)).id + "\t");
                } else {
                    System.out.print("  \t");
                }
            }
            System.out.println();
        }
    }
    
    public static void scheduleToFile(Schedule schedule, Instance instance) {
        
    }
}
