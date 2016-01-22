package hmo.projekt.structures.schedule;

import hmo.projekt.Instance;
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
    public int[] schedule;
    public int fitness;
    
// pretpostavljam da će mi trebati kod zamjena dana
    public int maxWeekends;
    
    public int daysToMaxDay;
    public int daysToMinDay;
    
    public int leftoverWorkDays ;

    public WorkerSchedule(int[] schedule, int workerId, int maxWeekends, int daysToMaxDay, int daysToMinDay) {
        
        this.schedule = schedule;
        this.workerId = workerId;
        this.maxWeekends = maxWeekends;
        this.daysToMinDay = daysToMinDay;
        this.daysToMaxDay = daysToMaxDay;
    }

    public WorkerSchedule(WorkerSchedule workerSchedule) {
        
        this.schedule = workerSchedule.schedule;
        this.workerId = workerSchedule.workerId;
        this.maxWeekends = workerSchedule.maxWeekends;
        this.daysToMinDay = workerSchedule.daysToMinDay;
        this.daysToMaxDay = workerSchedule.daysToMaxDay;
        this.fitness = workerSchedule.fitness;
    }
   
// izračunava fitness za razmještaj tog korisnika

    public void calculateFitness(Worker worker, int numberOfDays) {

        this.fitness = 0;
        
        for (Map.Entry<Integer, Request> shiftOnRequest : worker.shiftOnRequest.entrySet()) {
            Integer day = shiftOnRequest.getKey();
            Request request = shiftOnRequest.getValue();
            
            if (     this.schedule[day] == -1 
                || ( this.schedule[day] != -1 && this.schedule[day] != request.shift ) ) {
                this.fitness += request.weight;
            }
        }
        
        for (Map.Entry<Integer, Request> shiftOffRequest : worker.shiftOffRequest.entrySet()) {
            Integer day = shiftOffRequest.getKey();
            Request request = shiftOffRequest.getValue();
            
            if ( (this.schedule[day] != -1) && (this.schedule[day] == request.shift ) ) {
                this.fitness += request.weight;
            }
        }
    }
    
    public void debug (Instance instance) {
        
        for(int day = 0; day < instance.numberOfDays;day ++) {
            if (schedule[day] != -1) {
                System.out.print(day + " " + schedule[day]);
            } else {
                System.out.print(day + " Ne radim!");
            }
            
            if(instance.weekendShiftsSaturday.contains(day)) {
                System.out.println( "\t\tSubota");
            } else if (instance.weekendShiftsSunday.contains(day)) {
                System.out.println( "\t\tNedjelja");
            } else {
                System.out.println();
            }
        }
    }
}
