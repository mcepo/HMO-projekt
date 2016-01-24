package hmo.projekt.GeneticAlgorithm;

import hmo.projekt.Instance;
import hmo.projekt.PrettyPrint;
import hmo.projekt.structures.instance.Worker;
import hmo.projekt.structures.schedule.StaffSchedule;
import hmo.projekt.structures.schedule.WorkerSchedule;

/**
 *
 * @author Marko
 */
public class Corrections {
    
    
    public void apply (StaffSchedule staffSchedule, Instance instance) {
        
        // NIJE IŠLO
//        staffSchedule.calculateShiftCover(instance.shiftCover);
//        
//        for(WorkerSchedule workerSchedule : staffSchedule.workerSchedules){
//            
//            while( this.balanceWorker(   workerSchedule, 
//                                        instance, 
//                                        instance.staff.get(workerSchedule.workerId),
//                                        staffSchedule.spaceInShift
//                                    ) == true 
//            ) {
                
                
//                workerSchedule.calculateFitness(instance.staff.get(workerSchedule.workerId), 
//                                                instance.numberOfDays);
//                int test = 0;
//                for (int i = 0;i< workerSchedule.schedule.length;i++){
//                    if (workerSchedule.schedule[i] != -1){
//                        test ++;
//                    }
//                }
//                if (test != workerSchedule.workload) {
//                    PrettyPrint.workerSchedule(workerSchedule.schedule, instance, workerSchedule.workerId);
//                                
//            System.out.println(
//                instance.staff.get(workerSchedule.workerId).id + 
//                " { " + 
//                instance.staff.get(workerSchedule.workerId).maxShifts + 
//                " > " + 
//                workerSchedule.workload + 
//                 " ( " + test + " ) " +       
//                " > " + 
//                instance.staff.get(workerSchedule.workerId).minShifts+
//                "}"
//                );
//            System.exit(-1);
//                }
//                
//            }
//        }
        staffSchedule.calculateShiftCover(instance.shiftCover);
        
        this.balanceDayShifts(staffSchedule, instance);
        
        staffSchedule.calculateShiftCover(instance.shiftCover);
    }
    
    public boolean balanceWorker (WorkerSchedule workerSchedule, Instance instance, Worker worker, int[][] spaceInShift) {
        
        int daysOff = 0;
        int daysOn = 0;
        
        boolean hadChanges = false ;
        
    //    PrettyPrint.workerSchedule(workerSchedule.schedule, instance, workerSchedule.workerId);
            
            for(int day = 1; day < workerSchedule.schedule.length; day ++ ) {
                
                if (workerSchedule.schedule[day] == -1 ){
 // neradan dan
                    daysOff ++;
             // pokušaj prethodni dan pretvoriti u neradni 
                            // provjeri dali je prethodni dan neradni
                    if (       workerSchedule.schedule[day -1] != -1  
                            // jel odrađen dovoljan broj smjena
                            && daysOn >  worker.minConsecutiveShifts 
                            // jel imam još dana za dodjeliti tom čovjeku
                            &&  workerSchedule.workload > worker.minShifts
                            // dali ima viška u prethodnoj smjeni
                            &&  spaceInShift[day-1][workerSchedule.schedule[day-1]] < 0
                            ) {
                    //    System.out.println("MAKNUO NA DAN " + (day-1));
                        // povećaj mjesta u tom danu
                        spaceInShift[day-1][workerSchedule.schedule[day-1]] ++; 
                        // makni čovjeka iz toga dana
                        workerSchedule.schedule[day-1] = -1;
                        // smanji mu workload, manje dana radi
                        workerSchedule.workload --;
                        // kod dodavanja za 
                        if ( instance.weekendShiftsSaturday.contains(day-1) == true ||
                                (instance.weekendShiftsSunday.contains(day-1) == true 
                                    && daysOn < 2)){
                            workerSchedule.maxWeekends ++;
                        }
                        
                        hadChanges = true;
                    }
                    
                    daysOn = 0;
                }else {
// radni dan
                    daysOn++;
          /// pokušaj prethodni dan pretvoriti u radni          
                    if (    workerSchedule.schedule[day -1] == -1
                            && daysOff > worker.minConsecutiveDaysOff 
                            && workerSchedule.workload < worker.maxShifts 
                            && (
                            ( ( instance.weekendShiftsSunday.contains(day-1) == true ||
                                    (
                                        instance.weekendShiftsSaturday.contains(day-1) == true 
                                        && daysOff < 2
                                    )
                                )
                                && workerSchedule.maxWeekends != 0) 
                                || instance.weekendShiftsSunday.contains(day-1) == false) 
                            && (
                                (day + worker.maxConsecutiveShifts) > instance.numberOfDays 
                                    || (
                                        (( day + worker.maxConsecutiveShifts) < instance.numberOfDays)
                                        && workerSchedule.schedule[day + worker.maxConsecutiveShifts -1 ] == -1
                                        && workerSchedule.schedule[day + (int)worker.maxConsecutiveShifts/2 +1] == -1
                                )
                            )
                            && !worker.daysOff.contains(day-1)
                        ){
                        
                        for(Integer shift : worker.canWorkShift) {
                            if (spaceInShift[day-1][shift] > 0){
                     //           System.out.println(" ******** DODAO U DAN " + (day -1));
                                // povećaj mjesta u tom danu
                                spaceInShift[day-1][shift] --; 
                                // makni čovjeka iz toga dana
                                workerSchedule.schedule[day-1] = shift;
                                // smanji mu workload, manje dana radi
                                workerSchedule.workload ++;
                                
                                if ( instance.weekendShiftsSunday.contains(day-1) == true ||
                                (instance.weekendShiftsSaturday.contains(day-1) == true 
                                    && daysOff < 2)){
                                    workerSchedule.maxWeekends --;
                                }
                                hadChanges = true;
                            }
                            break;
                        }
                    }
                    daysOff = 0;
                }
            }
       
            // provjera valjanosti rješenja
            daysOn = 0;
            daysOff = 0;
            

            for(int day = 0 ;day < instance.numberOfDays;day++){
                if (workerSchedule.schedule[day] == -1){
                    daysOff ++;
                    if ((daysOn > 0 && daysOn < worker.minConsecutiveShifts) || daysOn > worker.maxConsecutiveShifts){
                        System.out.println("false consecutiveShifts " + daysOn + " maxShifts " + worker.maxConsecutiveShifts);
                        PrettyPrint.workerSchedule(workerSchedule.schedule, instance, workerSchedule.workerId);
                        System.exit(-1);
                    }
                        daysOn = 0;
                } else {
                    daysOn ++;
                    if (daysOff > 0 && daysOff < worker.minConsecutiveDaysOff){
                        System.out.println("false consecutiveDaysOff");
                        PrettyPrint.workerSchedule(workerSchedule.schedule, instance, workerSchedule.workerId);
                        System.exit(-1);

                    }
                    daysOff = 0;
                }
            }
            if ((worker.maxShifts >= workerSchedule.workload) 
                    && (workerSchedule.workload >= worker.minShifts) 
                    && workerSchedule.maxWeekends >= 0) {
         //       System.out.println("OK " + instance.staff.get(workerSchedule.workerId).id);
                } else {
                    System.out.println("false " + workerSchedule.workload + " maxWeekends " + workerSchedule.maxWeekends + " allowed weekends " + worker.maxWeekends ) ;
                    PrettyPrint.workerSchedule(workerSchedule.schedule, instance, workerSchedule.workerId);
                    System.exit(-1);
                }
            
            return hadChanges;
    }
    
    public void balanceDayShifts( StaffSchedule staffSchedule, Instance instance ) { 
        
        for(WorkerSchedule workerSchedule : staffSchedule.workerSchedules){
 
            Worker worker = instance.staff.get(workerSchedule.workerId);
            
            for(int day = 0; day < workerSchedule.schedule.length; day ++ ) {
                
                if ((workerSchedule.schedule[day] == -1) || (staffSchedule.spaceInShift[day][workerSchedule.schedule[day]] >= 0) ) { continue; }
// imam previše ljudi u toj smjeni
          //      System.out.println(" ************************************************************************* ");
          //      System.out.println(day + " -> Prošao (ima previše ljudi u smjeni) " + staffSchedule.spaceInShift[day][workerSchedule.schedule[day]] + " smjena " + workerSchedule.schedule[day]);
                    
                for(Integer shift : worker.canWorkShift) {
                    if (staffSchedule.spaceInShift[day][shift] <= 0) { continue; }
// ima smjena u koju mogu dodati
             // ako je nova smjena manja od trenutne smjene
                    if ( (day == 0 || workerSchedule.schedule[day -1] == -1 || workerSchedule.schedule[day -1 ] <= shift) 
                            && 
                            (day == (workerSchedule.schedule.length - 1) || workerSchedule.schedule[day +1] == -1 || workerSchedule.schedule[day + 1 ] >= shift)
                        ){ 
                   //         System.out.println("New shift " + shift + " mjesta " + staffSchedule.spaceInShift[day][shift]);
                   //         System.out.println("Old shift " + workerSchedule.schedule[day] + " mjesta " + staffSchedule.spaceInShift[day][workerSchedule.schedule[day]]);         
                            
                            ++ staffSchedule.spaceInShift[day][workerSchedule.schedule[day]];
                    //        System.out.println("Povećavam mjesta u smjeni " + workerSchedule.schedule[day] + " ostalo mjesta " + staffSchedule.spaceInShift[day][workerSchedule.schedule[day]]);

                            -- staffSchedule.spaceInShift[day][shift] ;
                    //       System.out.println("Smanjujem mjesto u smjeni " + shift + " ostalo mjesta " + staffSchedule.spaceInShift[day][shift]);

                            workerSchedule.schedule[day] = shift;
                   //         System.out.println("Zamjena " + workerSchedule.schedule[day]);

                        }
                    break;
                }  
            }
            workerSchedule.calculateFitness(worker, instance.numberOfDays);
         //   System.exit(-1);
        }
    }
}
