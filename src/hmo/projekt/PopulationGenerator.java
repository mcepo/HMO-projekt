package hmo.projekt;

import hmo.projekt.structures.instance.Worker;
import hmo.projekt.structures.schedule.Schedule;
import hmo.projekt.structures.schedule.WorkerSchedule;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Marko
 */
public class PopulationGenerator {
    
    private final Instance instance;
        
// generiranje inicijalne populacije

    public PopulationGenerator( Instance instance) {
        this.instance = instance;
    }

    public Schedule generateSchedule() {
        
        Schedule schedule = new Schedule();
        
        // ovdje mora popunti strukturu staffSchedule
        
        WorkerSchedule workerSchedule;
        
        for(int i=0;i< this.instance.numberOfWorkers;i++){
            while ((workerSchedule = this.generateInitialWorkerSchedule(i)) == null ) { 
          //      System.out.println("Generiram raspored za radnika " + i);
            }
            schedule.workerSchedules.add(workerSchedule);
        }
        
        return schedule;
    }
    
    public WorkerSchedule generateInitialWorkerSchedule ( int workerId ) {       
        
        Worker worker = this.instance.staff.get(workerId);
        int consecutiveDaysOn = 0;
        int consecutiveDaysOff = 0;
        HashMap<Integer, Integer> workSchedule = new HashMap<>();
        int day = (int)(Math.random() * (worker.spreadDaysOff.get(worker.spreadDaysOff.size() - 1) +1));
        int shift ;
        int index;
        int maxWeekends = worker.maxWeekends;
        
        List<Integer> allowedShiftsInNextDay = new ArrayList<>(worker.canWorkShift);
        
        while (day < this.instance.numberOfDays && workSchedule.size() < worker.maxShifts) {
            
                if (worker.possibleDays.contains(day) == false ) { 
                    ++ day;
                    continue;
                }
                
                if (consecutiveDaysOn == 0) {
                    index = (int)(Math.random() * worker.spreadDaysOn.size());
                    consecutiveDaysOn = worker.spreadDaysOn.get(index);
                    consecutiveDaysOff = worker.spreadDaysOff.get(index);
                    allowedShiftsInNextDay = new ArrayList<>(worker.canWorkShift);
                    
                    if (day >= this.instance.numberOfDays) { break; }
                }
                
                if (this.instance.weekendShiftsSunday.contains(day) && workSchedule.containsKey( day - 1 ) == false ) {
                    ++ day; 
                    continue;
                }

                if ( this.instance.weekendShiftsSaturday.contains(day) && consecutiveDaysOn == 1 ) {
                    -- consecutiveDaysOn ;
                    ++ day;
                    continue;
                }
                
                shift = (int)(Math.random() * allowedShiftsInNextDay.size());  
                workSchedule.put(day, allowedShiftsInNextDay.get(shift));                
                this.instance.shiftCover.put(day*this.instance.numberOfShiftsPerDay+shift, this.instance.shiftCover.get(day*this.instance.numberOfShiftsPerDay+shift) -1 );
                
                if ( this.instance.weekendShiftsSaturday.contains(day)) {
                    if (maxWeekends == 0) {
                        return null ;
                    } else {
                    -- maxWeekends ;                        
                    }
                }
                
                if (this.instance.shifts.get(shift).cantFollowShift != null) {
                    for(index = 0; index < allowedShiftsInNextDay.size(); index ++) {
                        if (this.instance.shifts.get(shift).cantFollowShift.contains(allowedShiftsInNextDay.get(index))) {
                            allowedShiftsInNextDay.remove(index);
                            index --;
                        }
                    }
                }
                -- consecutiveDaysOn ;
                if ( consecutiveDaysOn == 0 ) {
                    day += consecutiveDaysOff;
                    
                    if (Math.random() > ((worker.minShifts + (worker.minShifts*0.25)) / worker.possibleDays.size()) ) {
                        day += 1;
                    }
                }
                
                ++ day;
        }
        
        
        // Ovo smisli kako ćeš preseliti u gornju petlju, da ne prolaziš bez veze ponovo kroz listu
        
        
        for (day = 0;day < workSchedule.size() ; day ++) {
            if (workSchedule.containsKey(day) == true) {
                if( workSchedule.containsKey( day - 1) == false && workSchedule.containsKey( day + 1 ) == false ){
                    workSchedule.remove(day);
                }
            }
        }
        
// DEBUG
        
//        for(day = 0; day < this.instance.numberOfDays;day ++) {
//            if (workSchedule.containsKey(day)) {
//                System.out.print(day + " " + workSchedule.get(day));
//            } else {
//                System.out.print(day + " Ne radim!");
//            }
//            
//            if(this.instance.weekendShiftsSaturday.contains(day)) {
//                System.out.println( "\t\tSubota");
//            } else if (this.instance.weekendShiftsSunday.contains(day)) {
//                System.out.println( "\t\tNedjelja");
//            } else {
//                System.out.println();
//            }
//        }
//        
//                System.out.println(
//                        worker.id + 
//                        " { " + 
//                        worker.maxShifts + 
//                        " > " + 
//                        workSchedule.size()+ 
//                        " > " + 
//                        worker.minShifts+
//                                " | weekends " +
//                                maxWeekends +
//                                "}"
//        );
                
        if ((worker.maxShifts >= workSchedule.size()) && (workSchedule.size() <= worker.minShifts) && maxWeekends >= 0) {
            return new WorkerSchedule(workSchedule);
        } else {
            return null;
        }
    }   
}