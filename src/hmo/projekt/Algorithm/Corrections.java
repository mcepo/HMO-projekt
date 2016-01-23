package hmo.projekt.Algorithm;

import hmo.projekt.Instance;
import hmo.projekt.structures.instance.Worker;
import hmo.projekt.structures.schedule.StaffSchedule;
import hmo.projekt.structures.schedule.WorkerSchedule;

/**
 *
 * @author Marko
 */
public class Corrections {
   
    
    public static void balanceDayShifts( StaffSchedule staffSchedule, Instance instance) { 
        
   //     System.out.println("Radim korekcije");
        
        staffSchedule.calculateShiftCover(instance.shiftCover);
        
        for(WorkerSchedule workerSchedule : staffSchedule.workerSchedules){
            
         //   workerSchedule.debug(instance);
            
      //      System.out.println("\n******************** worker " + workerSchedule.workerId + " **************");
            
            Worker worker = instance.staff.get(workerSchedule.workerId);
            
            for(int day = 0; day < workerSchedule.schedule.length; day ++ ) {
                
                if (workerSchedule.schedule[day] == -1 ) { continue; }
                
                if (staffSchedule.shiftCover[day][workerSchedule.schedule[day]] < 0) {
// imam previše ljudi u toj smjeni
               //     System.out.println(day + " -> Prošao (ima previše ljudi u smjeni) " + staffSchedule.shiftCover[day][workerSchedule.schedule[day]] + " smjena " + workerSchedule.schedule[day]);
                    
                    for(Integer shift : worker.canWorkShift) {
                        if (staffSchedule.shiftCover[day][shift] > 0) {
// ima smjena u koju mogu dodati
                     // ako je nova smjena manja od trenutne smjene
                            int tempDay;
                            if ( workerSchedule.schedule[day] > shift){
// moraš unazad provjeriti jel sve valja
                   //          System.out.println("Smjena " + shift + " fali ljudi, ima mjesta: " + shiftCover[day][shift]);
                                for(tempDay = day; (tempDay >= 0 &&  workerSchedule.schedule[tempDay] != -1) ; --tempDay ) {
                                    
                                    if ( workerSchedule.schedule[tempDay] > shift ){
                                                                                
                                        ++ staffSchedule.shiftCover[tempDay][workerSchedule.schedule[tempDay]];
                                        workerSchedule.schedule[tempDay] = shift;
                                        -- staffSchedule.shiftCover[tempDay][workerSchedule.schedule[tempDay]];
                                                                                
                                    }else {
                                        break;
                                    }
                                }
                            } else {
//  moraš unaprijed provjeriti jel sve valja
                    //       System.out.println("Smjena " + shift + " fali ljudi, ima mjesta: " + shiftCover[day][shift]);
                                for(tempDay = day; tempDay < workerSchedule.schedule.length && workerSchedule.schedule[tempDay] != -1 ;tempDay ++) {
                                    if ( workerSchedule.schedule[tempDay] < shift ){
                                        
                                        ++ staffSchedule.shiftCover[tempDay][workerSchedule.schedule[tempDay]];
                                        workerSchedule.schedule[tempDay] = shift;
                                        -- staffSchedule.shiftCover[tempDay][workerSchedule.schedule[tempDay]];
                                        
                                    }else {
                                        break;
                                    }
                                }
                            }
                            break;
                        } else {
                            
                        }
                    }   
                }
            }
        }
    }
}
