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
      
  //      System.out.println(this.staffSchedule.totalFitness);
        
//        for(WorkerSchedule ws : this.staffSchedule.workerSchedules){
//            System.out.println("\n******* ID " + ws.workerId + " ************************************* ");
//            ws.debug(instance);
//        }
        this.correctionsInSingleDay(this.staffSchedule);
  //      System.out.println(this.staffSchedule.totalFitness);
        
        return staffSchedule;
    }
    
    public WorkerSchedule generateWorkerSchedule ( int workerId ) {       
        
   //     System.out.println("*** Generiram populaciju");
        
        Worker worker = this.instance.staff.get(workerId);
        int consecutiveDaysOff = 0, consecutiveDaysOn;
        int[] schedule = new int[this.instance.numberOfDays];
// inicijalizacija, ovo treba nekako promjeniti        
        for(int i=0;i<schedule.length;i++){
            schedule[i] = -1;
        }
        int workDays = 0;
        int day = 0;
        
        if (Math.random() > 0.5 ){
            day = 0 ;
            consecutiveDaysOn = (int)(Math.random() * (worker.maxConsecutiveShifts - worker.minConsecutiveShifts + 1 ) + worker.minConsecutiveShifts);
            consecutiveDaysOff = 2;
      //      System.out.println("Consecutive days on " + consecutiveDaysOn);
        } else {
            day = (int)(Math.random() * worker.spreadDaysOff.get(worker.spreadDaysOff.size() - 1)) + 2;
      //      System.out.println("Initial free days " + day );
            consecutiveDaysOn = 0;
        }
        int shift ;
        int index;
        int maxWeekends = worker.maxWeekends;
        int dayOff = day;
        
        List<Integer> allowedShiftsInNextDay = new ArrayList<>(worker.canWorkShift);
        
        while (day < this.instance.numberOfDays && workDays <= worker.maxShifts ) {
            
            
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
           //     System.out.println("consecutiveDaysOn " + consecutiveDaysOn);
           //     System.out.println("consecutiveDaysOff " + consecutiveDaysOff);
            }
// ako počinjem na nedjelji preskoči je
            if (this.instance.weekendShiftsSunday.contains(day) && schedule[day - 1] == -1 ) {
         //       System.out.println("Usamljena nedjelja");
                ++ day; 
                continue;
            }
// ako završavam na suboti preskoči je
            if ( this.instance.weekendShiftsSaturday.contains(day) && consecutiveDaysOn == 1 ) {
        //        System.out.println("Usamljena subota");
                -- consecutiveDaysOn ;
                ++ day;
                continue;
            }
            shift = allowedShiftsInNextDay.get((int)(Math.random() * allowedShiftsInNextDay.size())); 
            schedule[day] = shift;
            workDays ++;

            if ( this.instance.weekendShiftsSaturday.contains(day) || 
                 ( this.instance.weekendShiftsSunday.contains(day) &&  schedule[day-1] == -1)) {
                if (maxWeekends == 0) {
                    return null ;
                } else {
                   -- maxWeekends ;                        
                }
             //   -- maxWeekends;
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
    //    System.out.println(worker.minConsecutiveShifts);
 //izbaci "samostojeće" radne dane
        for(day = 1;day < schedule.length - 1;day ++) {
        
            if (schedule[day] != -1 ) {
                ++ consecutiveDaysOn;
                
            } else {
                if (consecutiveDaysOn < worker.minConsecutiveShifts) {
                    for (;consecutiveDaysOn != 0; consecutiveDaysOn --) {
                        schedule[day - consecutiveDaysOn] = -1;
                        -- workDays;
                    }
                }
                consecutiveDaysOn = 0;
            }
        }

        int daysOn = 0;
        int daysOff = 0;
        
        for(day = 0 ;day < this.instance.numberOfDays;day++){
            if (schedule[day] == -1){
                daysOff ++;
                if ((daysOn > 0 && daysOn < worker.minConsecutiveShifts) || daysOn > worker.maxConsecutiveShifts){
             //       PrettyPrint.workerSchedule(schedule, instance, worker.id);
           //         System.out.println("Imam samostojeći radnih dana " + worker.id);
                    return null;
                }
                    daysOn = 0;
            } else {
                daysOn ++;
                if (daysOff > 0 && daysOff < worker.minConsecutiveDaysOff){
               //     PrettyPrint.workerSchedule(schedule, instance, worker.id);
            //        System.out.println("Imam samostojeći slobodan dan " + worker.id);
                    return null;
                }
                daysOff = 0;
            }
        }
        
        
    //    PrettyPrint.workerSchedule(schedule, instance);

//
//            if ((worker.maxShifts >= workDays) && (workDays >= worker.minShifts) && maxWeekends >= 0) { 
//        System.out.println(" *********** POGODAK ************");
//        PrettyPrint.workerSchedule(schedule, instance, worker.id);
//        
//                        System.out.println(
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
//        
//     System.exit(-1);
//    }
        
//            
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
            
     //   return null;
     //   System.exit(-1);
        
        if ((worker.maxShifts >= workDays) && (workDays > worker.minShifts) && maxWeekends >= 0) {
//                    System.out.println(
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
            
            return new WorkerSchedule(  schedule, 
                                        workerId, 
                                        maxWeekends, 
                                        ( /* worker.maxShifts - */workDays ),
                                        ( workDays - worker.minShifts) );
        } else {
            return null;
        }
    }

    public void correctionsInSingleDay( StaffSchedule staffSchedule) { 
        
        System.out.println("Radim korekcije");
        
        for(WorkerSchedule workerSchedule : staffSchedule.workerSchedules){
            
         //   workerSchedule.debug(instance);
            
       //     System.out.println("\n******************** worker " + workerSchedule.workerId + " **************");
            
            Worker worker = this.instance.staff.get(workerSchedule.workerId);
            int[][] shiftCover = this.staffSchedule.shiftCover;
            
            for(int day = 0; day < workerSchedule.schedule.length; day ++ ) {
                
                if (workerSchedule.schedule[day] == -1 ) { continue; }
                
                if (shiftCover[day][workerSchedule.schedule[day]] < 0) {
// imam previše ljudi u toj smjeni
             //       System.out.println(day + " -> Prošao (ima previše ljudi u smjeni) " + this.staffSchedule.shiftCover[day][workerSchedule.schedule[day]] + " smjena " + workerSchedule.schedule[day]);
                    
                    for(Integer shift : worker.canWorkShift) {
                        if (shiftCover[day][shift] > 0) {
// ima smjena u koju mogu dodati
                     // ako je nova smjena manja od trenutne smjene
                            int tempDay;
                            if ( workerSchedule.schedule[day] > shift){
// moraš unazad provjeriti jel sve valja
               //              System.out.println("Smjena " + shift + " fali ljudi, ima mjesta: " + shiftCover[day][shift]);
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
                        } else {
 // ako nema probaj izbaciti ga iz rasporeda
//                            if (day == 0){
//                                
//                            } else if (day == (workerSchedule.schedule.length -1) ) {
//                                
//                            } else {
//                                
//                            }
//                            if (workerSchedule.schedule[ day -1 ] == workerSchedule.schedule[day +1] )
////                            
//                            int daysOffLeft = 0;
//                            int daysOffRight = 0;
//                            
//                            for(int leftDay = day - 1, rightDay = day + 1; (leftDay >= 0 && rightDay < workerSchedule.schedule.length); leftDay--, rightDay++) {
//                                
//                                if (workerSchedule.schedule[])
//                                
//                            }
                        }
                    }   
                }
            }
        }
        this.staffSchedule.calculateFitnessWithCover(this.instance.weightForShiftCoverUnder, this.instance.weightForShiftCoverOver);
    }
}