package hmo.projekt;

import hmo.projekt.structures.schedule.StaffSchedule;
import hmo.projekt.structures.schedule.WorkerSchedule;

/**
 *
 * @author Marko
 */
public class Crossover {
    
    public StaffSchedule apply(StaffSchedule first, StaffSchedule second, Instance instance){
        
        StaffSchedule result = new StaffSchedule(instance.numberOfDays, instance.numberOfShiftsPerDay, instance.numberOfWorkers); 
        int crossPoint = (int)(Math.random() * first.workerSchedules.length);
        merge(first.workerSchedules, result.workerSchedules, 0, crossPoint);
        merge(second.workerSchedules, result.workerSchedules, crossPoint, second.workerSchedules.length);
        return result;
    }

    private void merge(WorkerSchedule[] workerSchedule, WorkerSchedule[] result, int start, int end) {
        for( int i = start; i < end; i++){
            WorkerSchedule copyWorkerSchedule = new WorkerSchedule( workerSchedule[i] );
            result[i] = copyWorkerSchedule;
        }
    }
}
