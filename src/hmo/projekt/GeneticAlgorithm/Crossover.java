package hmo.projekt.GeneticAlgorithm;

import hmo.projekt.Instance;
import hmo.projekt.structures.schedule.StaffSchedule;
import hmo.projekt.structures.schedule.WorkerSchedule;

/**
 *
 * @author Marko
 */
public class Crossover {
    
    public void apply(StaffSchedule first, StaffSchedule second, Instance instance, StaffSchedule result){
        
        int crossPoint = (int)(Math.random() * first.workerSchedules.length);
        merge(first.workerSchedules, result.workerSchedules, 0, crossPoint);
        merge(second.workerSchedules, result.workerSchedules, crossPoint, second.workerSchedules.length);
    }

    private void merge(WorkerSchedule[] workerSchedule, WorkerSchedule[] result, int start, int end) {
        for( int i = start; i < end; i++){
            WorkerSchedule copyWorkerSchedule = new WorkerSchedule( workerSchedule[i] );
            result[i] = copyWorkerSchedule;
        }
    }
}
