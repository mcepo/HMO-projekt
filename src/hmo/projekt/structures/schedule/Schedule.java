package hmo.projekt.structures.schedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Marko
 */
public class Schedule {
    
    public List<WorkerSchedule> workerSchedules;
    
    HashMap<Integer, Integer> shiftFitness;
    int totalFitness;
    
    public Schedule (){
        this.workerSchedules = new ArrayList<>();
    } 
}
