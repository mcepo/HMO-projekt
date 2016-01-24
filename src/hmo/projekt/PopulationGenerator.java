package hmo.projekt;

import hmo.projekt.GeneticAlgorithm.Corrections;
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
    
    public Corrections corrections;

    public PopulationGenerator( Instance instance) {
        this.instance = instance;
        this.corrections = new Corrections();
    }

    public StaffSchedule generateStaffSchedule() {
        
        this.staffSchedule = new StaffSchedule(this.instance);
        
        WorkerSchedule workerSchedule;
        
        for(int workerId=0;workerId < this.instance.numberOfWorkers; workerId++){
            
            while ((workerSchedule = this.generateWorkerSchedule(workerId)) == null ) { }
            workerSchedule.calculateFitness(this.instance.staff.get(workerId), this.instance.numberOfDays);
            this.staffSchedule.workerSchedules[workerId] = workerSchedule;
        }
        this.staffSchedule.calculateShiftCover(instance.shiftCover);
     //   this.corrections.removeWorkers(staffSchedule, instance);
        this.corrections.balanceDayShifts(this.staffSchedule, instance);
        this.staffSchedule.calculateShiftCover(instance.shiftCover);
                
        this.staffSchedule.calculateFitness(this.instance);
        return staffSchedule;
    }
    
    public WorkerSchedule generateWorkerSchedule ( int workerId ) {       
        
        Worker worker = this.instance.staff.get(workerId);
        int consecutiveDaysOff = 0, consecutiveDaysOn;
        int[] schedule = new int[this.instance.numberOfDays];
  
        for(int i=0;i<schedule.length;i++){
            schedule[i] = -1;
        }
        
        int workload = 0;
        int day = 0;
        
        if (Math.random() > 0.5 ){
            day = 0 ;
            consecutiveDaysOn = (int)(Math.random() * (worker.maxConsecutiveShifts - worker.minConsecutiveShifts + 1 ) + worker.minConsecutiveShifts);
            consecutiveDaysOff = 2;
        } else {
            day = (int)(Math.random() * worker.spreadDaysOff.get(worker.spreadDaysOff.size() - 1)) + 2;
            consecutiveDaysOn = 0;
        }
        int shift ;
        int index;
        int maxWeekends = worker.maxWeekends;
        int dayOff = day;
        
        List<Integer> allowedShiftsInNextDay = new ArrayList<>(worker.canWorkShift);
        
        while (day < this.instance.numberOfDays && workload <= worker.maxShifts ) {

            if (worker.daysOff.contains(day) == true ) { 
                ++ day;
                consecutiveDaysOn = (int)(Math.random() * (worker.maxConsecutiveShifts - worker.minConsecutiveShifts + 1 ) + worker.minConsecutiveShifts);
                ++ dayOff;
                continue;
            }
            
            if (dayOff < worker.minConsecutiveDaysOff && dayOff != 0) {
                day += worker.minConsecutiveDaysOff - dayOff;
                dayOff = 0;
                continue;
            }
            dayOff = 0;

            if (consecutiveDaysOn == 0) {
                index = (int)(Math.random() * worker.spreadDaysOn.size());
                consecutiveDaysOn = worker.spreadDaysOn.get(index);
                consecutiveDaysOff = worker.spreadDaysOff.get(index);
                if (Math.random() > ((worker.minShifts + (worker.minShifts*0.20)) / worker.daysOff.size()) ) {
                    consecutiveDaysOn -= 1;
                }
                if (Math.random() > ((worker.minShifts + (worker.minShifts*0.20)) / worker.daysOff.size()) ) {
                    consecutiveDaysOff += 1;
                }
                allowedShiftsInNextDay = new ArrayList<>(worker.canWorkShift);
            }
// ako počinjem na nedjelji preskoči je
            if (this.instance.weekendShiftsSunday.contains(day) && schedule[day - 1] == -1 ) {
                ++ day; 
                continue;
            }
// ako završavam na suboti preskoči je
            if ( this.instance.weekendShiftsSaturday.contains(day) && consecutiveDaysOn == 1 ) {
                -- consecutiveDaysOn ;
                ++ day;
                continue;
            }
            shift = allowedShiftsInNextDay.get((int)(Math.random() * allowedShiftsInNextDay.size())); 
            schedule[day] = shift;
            
            workload ++;

            if ( this.instance.weekendShiftsSaturday.contains(day) || 
                 ( this.instance.weekendShiftsSunday.contains(day) &&  schedule[day-1] == -1)) {
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
            }
            ++ day;
        }
        
        consecutiveDaysOn = 0;
        for(day = 1;day < schedule.length - 1;day ++) {
        
            if (schedule[day] != -1 ) {
                ++ consecutiveDaysOn;
                
            } else {
                if (consecutiveDaysOn < worker.minConsecutiveShifts) {
                    for (;consecutiveDaysOn != 0; consecutiveDaysOn --) {
                        schedule[day - consecutiveDaysOn] = -1;
                        -- workload;
                    }
                }
                consecutiveDaysOn = 0;
            }
        }
        
// provjera valjanosti rješenja, treba to promjeniti
        int daysOn = 0;
        int daysOff = 0;
        
        for(day = 0 ;day < this.instance.numberOfDays;day++){
            if (schedule[day] == -1){
                daysOff ++;
                if ((daysOn > 0 && daysOn < worker.minConsecutiveShifts) || daysOn > worker.maxConsecutiveShifts){
                    return null;
                }
                    daysOn = 0;
            } else {
                daysOn ++;
                if (daysOff > 0 && daysOff < worker.minConsecutiveDaysOff){
                    return null;
                }
                daysOff = 0;
            }
        }

        if ((worker.maxShifts >= workload) && (workload > worker.minShifts) && maxWeekends >= 0) {
     
        
        // SAMO GORNJU LINIJU ZAKOMENTRAJ I DOLJNJU LINIJU ODKOMENTIRAJ
     //   if (Algorithm.isFeasible(schedule, instance, workerId)) {    
            return new WorkerSchedule(  schedule, 
                                        workerId, 
                                        maxWeekends, 
                                        workload);
        } else {
            return null;
        }
    }
    
    private void dumpGeneratedWorkerSchedule (Worker worker, int maxWeekends, int workDays) {
        System.out.println(
                worker.id + 
                " { " + 
                worker.maxShifts + 
                " > " + 
                workDays+ 
                " > " + 
                worker.minShifts+
                " | weekends " +
                maxWeekends +
                "}"
        );
    }
}