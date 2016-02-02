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
        this.corrections.apply(staffSchedule, instance);
        
        return staffSchedule;
    }
    
    public WorkerSchedule generateWorkerSchedule ( int workerId ) {       
        
        Worker worker = this.instance.staff.get(workerId);
        int consecutiveDaysOff = 0, consecutiveDaysOn;
        int[] schedule = new int[this.instance.numberOfDays];
  
        for(int i=0;i<schedule.length;i++){
            schedule[i] = -1;
        }
        
        int day;
        
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
        int dayOff = day;
        int workload = 0;
        
        List<Integer> allowedShiftsInNextDay = new ArrayList<>(worker.canWorkShift);
        
        while (day < this.instance.numberOfDays && workload <= worker.maxShifts) {

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
                if (Math.random() > ((double)(worker.maxShifts*1.5)/instance.numberOfDays )) {
                    if (consecutiveDaysOn-1 < worker.minConsecutiveShifts) {
                        consecutiveDaysOn -= 1;
                    }
                }
                if (Math.random() > ((double)(worker.minShifts*1.5)/instance.numberOfDays) ) {
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
            workload ++ ;

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
                    }
                }
                consecutiveDaysOn = 0;
            }
        }

        return this.workerScheduleIsFeasible(schedule, instance, workerId);
    }
    
        
    public WorkerSchedule workerScheduleIsFeasible (int[] schedule, Instance instance, int workerId) {
	// provjera jedne smjene dnevno nije potrebna, jer struktura ne dozvoljava vise od jedne
        
// trenutni radnik
        Worker worker = instance.staff.get(workerId);

// pronadi ukupno vrijeme rada za tog radnika
        int workload = 0;
        int numberOfWorkWeekends = 0;
        
        boolean weekend = false;
        
        String errorMsg = "";
        
        WorkerSchedule workerSchedule = null;
        
        int day, daysOn, daysOff;

// iteriraj po workerSchedules.schedule (po danima), svaka iteracija je jedan dan
// odnosno smjena koju je radnik odradio taj dan
        for (day = 0, daysOn = 0, daysOff = 0; day < schedule.length; day ++) {
            
    // kad pročitaš ne radni dan
            if(schedule[day] == -1 ){ // neradni dan
                
        // provjeri dali je imao adekvatan broj uzastopnih radnih dana
                if (    daysOn != 0 
                     && daysOn > worker.maxConsecutiveShifts 
                     && daysOn < worker.minConsecutiveShifts
                    ) {
                    // nema adekvatan broj uzastopnih dana
                    errorMsg = " uzastopni radni dani izvan ograničenja: " +
                                worker.maxConsecutiveShifts +
                                " > " +
                                daysOn +
                                " > " +
                                worker.minConsecutiveShifts +
                                "\n\t";
                    break;
                }
                
                daysOff++;
                daysOn = 0;
                weekend = false;
                
            } else { // radni dan
                
        // provjeri dali je imao adekvatan broj uzastopnih slobodnih dana        
                if (    daysOff != 0 
                     && daysOff < worker.minConsecutiveDaysOff
                    ) {
                // radnik ne može raditi u dodjeljenoj smjeni
                    errorMsg = " premalo uzastopnih slobodnih dana: " +
                                daysOff +
                                " > " +
                                worker.minConsecutiveDaysOff + 
                                "\n\t";
                    break;
                }
                             
        // provjeri dali je vikend i pribroji vikend u radne vikende
                if ( instance.weekendShiftsSaturday.contains(day) == true ) {
                    weekend = true;
                    numberOfWorkWeekends ++;
                }
                
                if (instance.weekendShiftsSunday.contains(day) == true && weekend == false) {
                    numberOfWorkWeekends ++;
                }
                
                                
                if (worker.canWorkShift.contains(schedule[day]) == false) {
                       // radnik ne može raditi u dodjeljenoj smjeni
                    errorMsg = " radniku dodjeljena zabranjena smjena: " +
                                schedule[day] +
                                " dozvoljene smjene: " +
                                worker.canWorkShift +
                                "\n\t";
                    break;
                }

                daysOn++;
                workload++;
                daysOff = 0;
            }
        }
        
        
     // provjeri jel ima previše vikenda u rasporedu        
        if (numberOfWorkWeekends > worker.maxWorkWeekends) {
            errorMsg += " previše vikenda " + 
                         worker.maxWorkWeekends +
                       " >= " +
                         numberOfWorkWeekends + 
                        "\n\t";
        }
     // provjeri jel ima previše radnih dana u rasporedu   
        if ( worker.minShifts > workload || workload > worker.maxShifts ) {
            errorMsg += " uzastopni radni dani izvan ograničenja: " +
                        worker.maxShifts +
                        " > " +
                        workload +
                        " > " +
                        worker.minShifts;
        }
        
        if (errorMsg.equals("")) {
            // sve je uredu
            workerSchedule = new WorkerSchedule(schedule, workerId, numberOfWorkWeekends, workload);
//            System.out.println(
//                worker.id + 
//                " { " + 
//                worker.maxShifts + 
//                " > " + 
//                workload+ 
//                " > " + 
//                worker.minShifts+
//                " | weekends " +
//                numberOfWorkWeekends +
//                "} - OK"
//            );
        } else {
            // ispiši grešku
        //    PrettyPrint.workerSchedule(schedule, instance, workerId);;
      //     System.out.println(worker.id + " - " + day + " - " + errorMsg);
        }
        return workerSchedule;                
    }
}