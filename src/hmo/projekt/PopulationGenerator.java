package hmo.projekt;

import hmo.projekt.structures.instance.Worker;
import hmo.projekt.structures.schedule.StaffSchedule;
import hmo.projekt.structures.schedule.WorkerSchedule;
import java.util.ArrayList;
import java.util.HashSet;
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
      
  //      System.out.println(this.staffSchedule.totalFitness);
        
//        for(WorkerSchedule ws : this.staffSchedule.workerSchedules){
//            System.out.println("\n******* ID " + ws.workerId + " ************************************* ");
//            ws.debug(instance);
//        }
        this.correctionsInSingleDay();
  //      System.out.println(this.staffSchedule.totalFitness);
        
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
                
                if (schedule[day -1] != -1 && schedule[day -2] == -1){
                    schedule[day -1 ] = -1;
                }
                
                consecutiveDaysOn = 0;
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

                if (Math.random() > ((worker.minShifts + (worker.minShifts*0.25)) / worker.possibleDays.size()) ) {
                    day += 1;
                }
            }

            ++ day;
        }
        
        consecutiveDaysOn = 0;
        
        for(day = initDay;day < schedule.length;day ++) {
            
            if (schedule[day] == -1 ) {
                if( consecutiveDaysOn < worker.minConsecutiveShifts && consecutiveDaysOn != 0) {
                    schedule[day -1 ] = -1 ;
                }
                consecutiveDaysOn = 0;
            } else {
                consecutiveDaysOn ++;
            }
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

    private void correctionsInSingleDay() { 
        
        for(WorkerSchedule workerSchedule : this.staffSchedule.workerSchedules){
            
         //   workerSchedule.debug(instance);
            
       //     System.out.println("\n******************** worker " + workerSchedule.workerId + " **************");
            
            HashSet <Integer> canWorkShift = this.instance.staff.get(workerSchedule.workerId).canWorkShift;
            int[][] shiftCover = this.staffSchedule.shiftCover;
            
            for(int day = 0; day < workerSchedule.schedule.length; day ++ ) {
                
                if (workerSchedule.schedule[day] == -1 ) { continue; }
                
                if (shiftCover[day][workerSchedule.schedule[day]] < 0) {
// imam previše ljudi u toj smjeni
       //             System.out.println(day + " -> Prošao (ima previše ljudi u smjeni) " + this.staffSchedule.shiftCover[day][workerSchedule.schedule[day]] + " smjena " + workerSchedule.schedule[day]);
                    
                    for(Integer shift : canWorkShift) {
                        if (shiftCover[day][shift] > 0) {
// ima smjena u koju mogu dodati
                     // ako je nova smjena manja od trenutne smjene
                            int tempDay = 0;
                            if ( workerSchedule.schedule[day] > shift){
// moraš unazad provjeriti jel sve valja
            //                 System.out.println("Smjena " + shift + " fali ljudi, ima mjesta: " + shiftCover[day][shift]);
                                for(tempDay = day; (tempDay >= 0 &&  workerSchedule.schedule[tempDay] != -1) ; --tempDay ) {
                                    
                                    if ( workerSchedule.schedule[tempDay] > shift ){
                                                                                
                                        ++ shiftCover[tempDay][workerSchedule.schedule[tempDay]];
                                        workerSchedule.schedule[tempDay] = shift;
                                        -- shiftCover[tempDay][workerSchedule.schedule[tempDay]];
                                                                                
                                    }else {
                                        break;
                                    }
                                }
                            } else {
//  moraš unaprijed provjeriti jel sve valja
             //               System.out.println("Smjena " + shift + " fali ljudi, ima mjesta: " + shiftCover[day][shift]);
                                for(tempDay = day; tempDay < workerSchedule.schedule.length && workerSchedule.schedule[tempDay] != -1 ;tempDay ++) {
                                    if ( workerSchedule.schedule[tempDay] < shift ){
                                        
                                        ++ shiftCover[tempDay][workerSchedule.schedule[tempDay]];
                                        workerSchedule.schedule[tempDay] = shift;
                                        -- shiftCover[tempDay][workerSchedule.schedule[tempDay]];
                                        
                                    }else {
                                        break;
                                    }
                                }
                            }
                            break;
                        }
                    }   
                }
            }
        }
        this.staffSchedule.calculateFitnessWithCover(this.instance.weightForShiftCoverUnder, this.instance.weightForShiftCoverOver);
    }
}