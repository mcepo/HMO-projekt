package hmo.projekt;

import hmo.projekt.structures.schedule.StaffSchedule;
import hmo.projekt.structures.schedule.WorkerSchedule;
import java.util.List;

/**
 *
 * @author Marko
 */
public class Crossover {
    
    public StaffSchedule apply(StaffSchedule first, StaffSchedule second, int[][] shiftCover){
        
        StaffSchedule result = new StaffSchedule(shiftCover); 
        int crossPoint = (int)(Math.random() * first.workerSchedules.size());
        merge(first.workerSchedules, result.workerSchedules, 0, crossPoint);
        merge(second.workerSchedules, result.workerSchedules, crossPoint, second.workerSchedules.size());
        return result;
    }

    private void merge(List<WorkerSchedule> workerSchedule, List<WorkerSchedule> result, int start, int end) {
        for( int i = start; i < end; i++){
            WorkerSchedule copyWorkerSchedule = new WorkerSchedule( workerSchedule.get(i) );
            result.add(copyWorkerSchedule);
        }
    }
}
