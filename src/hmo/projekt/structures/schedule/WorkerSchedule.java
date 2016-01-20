package hmo.projekt.structures.schedule;

import java.util.HashMap;

/**
 *
 * @author Marko
 */
public class WorkerSchedule {
    
    
    public HashMap<Integer, Integer> schedule;
    
    
    public WorkerSchedule (HashMap<Integer, Integer> schedule) {
        
        this.schedule = schedule;
    }   
}
