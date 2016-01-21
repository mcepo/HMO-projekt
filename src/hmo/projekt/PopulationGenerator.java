package hmo.projekt;

import hmo.projekt.structures.instance.Worker;
import hmo.projekt.structures.schedule.StaffSchedule;
import hmo.projekt.structures.schedule.WorkerSchedule;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Marko
 */
public class PopulationGenerator {
    
    private final Instance instance;
    public StaffSchedule staffSchedule;

    public PopulationGenerator( Instance instance) {
        this.instance = instance;
    }

    public StaffSchedule generateStaffSchedule() {
        
        this.staffSchedule = new StaffSchedule(this.instance.shiftCover);
        
        WorkerSchedule workerSchedule;
        
        for(int workerId=0;workerId < this.instance.numberOfWorkers; workerId++){
            
            while ((workerSchedule = this.generateWorkerSchedule(workerId)) == null ) { }
            workerSchedule.calculateFitness(this.instance.staff.get(workerId), this.instance.numberOfDays);
            this.staffSchedule.totalFitness += workerSchedule.fitness;
            this.staffSchedule.workerSchedules.add(workerSchedule);
        }
        
        this.staffSchedule.calculateFitness(this.instance.weightForShiftCoverUnder, this.instance.weightForShiftCoverOver);

        return staffSchedule;
    }
    
    public WorkerSchedule generateWorkerSchedule ( int workerId ) {       
        
        Worker worker = this.instance.staff.get(workerId);
        int consecutiveDaysOn = 0;
        int consecutiveDaysOff = 0;
        int[] schedule = new int[this.instance.numberOfDays];
// inicijalizacija, ovo treba nekako promjeniti        
        for(int i=0;i<schedule.length;i++){
            schedule[i] = -1;
        }
        int workDays = 0;
        
        int initDay = (int)(Math.random() * (worker.spreadDaysOff.get(worker.spreadDaysOff.size() - 1) +1));
        int day = initDay;
        int shift ;
        int index;
        int maxWeekends = worker.maxWeekends;
        
        List<Integer> allowedShiftsInNextDay = new ArrayList<>(worker.canWorkShift);
        
        while (day < this.instance.numberOfDays && workDays < worker.maxShifts ) {

            if (worker.possibleDays.contains(day) == false ) { 
                ++ day;
                continue;
            }

            if (consecutiveDaysOn == 0) {
                index = (int)(Math.random() * worker.spreadDaysOn.size());
                consecutiveDaysOn = worker.spreadDaysOn.get(index);
                consecutiveDaysOff = worker.spreadDaysOff.get(index);
                allowedShiftsInNextDay = new ArrayList<>(worker.canWorkShift);

           //     if (day >= this.instance.numberOfDays) { break; }
            }

            if (this.instance.weekendShiftsSunday.contains(day) && schedule[day - 1] == -1 ) {
                ++ day; 
                continue;
            }

            if ( this.instance.weekendShiftsSaturday.contains(day) && consecutiveDaysOn == 1 ) {
                -- consecutiveDaysOn ;
                ++ day;
                continue;
            }

            shift = (int)(Math.random() * allowedShiftsInNextDay.size()); 
            schedule[day] = allowedShiftsInNextDay.get(shift);
            workDays ++;

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
        
        int consecutiveWorkDays = 0;
        
        for(day = initDay;day < schedule.length;day ++) {
            
            if (schedule[day] == -1 ) {
                if( consecutiveWorkDays < worker.minConsecutiveShifts && consecutiveWorkDays != 0) {
                    schedule[day -1 ] = -1 ;
                }
                consecutiveWorkDays = 0;
            } else {
                consecutiveWorkDays ++;
            }
        }  
                            if (schedule[day - 1] == -1 && schedule [ day - 2 ] != -1 && schedule [day - 3] == -1 ){
                schedule[day - 2 ] = -1;
            }
        
//        System.out.println(
//                worker.id + 
//                " { " + 
//                worker.maxShifts + 
//                " > " + 
//                workDays+ 
//                " > " + 
//                worker.minShifts+
//                " | weekends " +
//                maxWeekends +
//                "}"
//        );
        
  //      PrettyPrint.workerSchedule(schedule, instance);
                
        if ((worker.maxShifts >= workDays) && (workDays <= worker.minShifts) && maxWeekends >= 0) {
            return new WorkerSchedule(  schedule, 
                                        workerId, 
                                        maxWeekends, 
                                        ( worker.maxShifts - workDays ),
                                        ( workDays - worker.minShifts) );
        } else {
            return null;
        }
    }
}