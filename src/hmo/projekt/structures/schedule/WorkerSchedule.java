package hmo.projekt.structures.schedule;

import hmo.projekt.structures.instance.Request;
import hmo.projekt.structures.instance.Worker;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Marko
 */
public class WorkerSchedule {
    
    public int workerId;
    public HashMap<Integer, Integer> schedule;
    public int fitness;
    
// pretpostavljam da će mi trebati kod zamjena dana
    public int maxWeekends;

    public WorkerSchedule(HashMap<Integer, Integer> schedule, int workerId, int maxWeekends) {
        
        this.schedule = schedule;
        this.workerId = workerId;
        this.maxWeekends = maxWeekends;
    }
   
// izračunava fitness za razmještaj tog korisnika

    public void calculateFitness(Worker worker, int numberOfDays) {

        this.fitness = 0;
        
        for (Map.Entry<Integer, Request> shiftOnRequest : worker.shiftOnRequest.entrySet()) {
            Integer day = shiftOnRequest.getKey();
            Request request = shiftOnRequest.getValue();
            
            if (     this.schedule.containsKey(day) == false 
                || ( this.schedule.containsKey(day) == true && this.schedule.get(day) != request.shift ) ) {
                this.fitness += request.weight;
            }
            
        }
        
        for (Map.Entry<Integer, Request> shiftOffRequest : worker.shiftOffRequest.entrySet()) {
            Integer day = shiftOffRequest.getKey();
            Request request = shiftOffRequest.getValue();
            
            if ( (this.schedule.containsKey(day) == true) && (this.schedule.get(day) == request.shift ) ) {
                this.fitness += request.weight;
            }
        }
    }
}
