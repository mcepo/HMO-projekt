package hmo.projekt.GeneticAlgorithm;

import hmo.projekt.Instance;
import hmo.projekt.structures.instance.Worker;
import hmo.projekt.structures.schedule.StaffSchedule;
import hmo.projekt.structures.schedule.WorkerSchedule;

/**
 *
 * @author Marko
 */
public class Corrections {
    
    
    public void removePossibleWorkers(  StaffSchedule staffSchedule, Instance instance ) {
        
        int daysOff = 0;
        int daysOn = 0;
        
        staffSchedule.calculateShiftCover(instance.shiftCover);
        
        for(WorkerSchedule workerSchedule : staffSchedule.workerSchedules){
            
            Worker worker = instance.staff.get(workerSchedule.workerId);
            
            for(int day = 0; (day < workerSchedule.schedule.length && workerSchedule.daysToMinDay<= worker.minShifts); day ++ ) {
                
                if (workerSchedule.schedule[day] == -1 ){
                    daysOff ++;
                    daysOn = 0;
                } else {
                    daysOn ++;
                    daysOff = 0;
                }
            }
        }
    }
    
    public void balanceDayShifts( StaffSchedule staffSchedule, Instance instance ) { 
        
        staffSchedule.calculateShiftCover(instance.shiftCover);
        
        for(WorkerSchedule workerSchedule : staffSchedule.workerSchedules){
 
            Worker worker = instance.staff.get(workerSchedule.workerId);
            
            for(int day = 0; day < workerSchedule.schedule.length; day ++ ) {
                
                if ((workerSchedule.schedule[day] == -1) || (staffSchedule.spaceInShift[day][workerSchedule.schedule[day]] >= 0) ) { continue; }
// imam previše ljudi u toj smjeni
               //     System.out.println(day + " -> Prošao (ima previše ljudi u smjeni) " + staffSchedule.spaceInShift[day][workerSchedule.schedule[day]] + " smjena " + workerSchedule.schedule[day]);
                    
                for(Integer shift : worker.canWorkShift) {
                    if (staffSchedule.spaceInShift[day][shift] <= 0) { continue; }
// ima smjena u koju mogu dodati
             // ako je nova smjena manja od trenutne smjene
                    int tempDay;
                    if ( workerSchedule.schedule[day] > shift){
// moraš unazad provjeriti jel sve valja
     //                System.out.println("Smjena " + shift + " fali ljudi, ima mjesta: " + staffSchedule.spaceInShift[day][shift]);
                        for(tempDay = day; (tempDay >= 0 &&  workerSchedule.schedule[tempDay] != -1) ; --tempDay ) {

                            if ( workerSchedule.schedule[tempDay] > shift ){

             //                   System.out.println("****** NAZAD");
             //                   System.out.println("New shift " + shift);
             //                   System.out.println("Old shift " + workerSchedule.schedule[tempDay]);
                                staffSchedule.spaceInShift[tempDay][workerSchedule.schedule[tempDay]] ++;
             //                   System.out.println("Povećavam mjesta u smjeni " + workerSchedule.schedule[tempDay]);
                                workerSchedule.schedule[tempDay] = shift;
             //                   System.out.println("Zamjena " + workerSchedule.schedule[tempDay]);
                                staffSchedule.spaceInShift[tempDay][workerSchedule.schedule[tempDay]] --;
             //                   System.out.println("Smanjujem mjesto u smjeni " + workerSchedule.schedule[tempDay]);
                                
                            }else {
                                break;
                            }
                        }
                    } else {
//  moraš unaprijed provjeriti jel sve valja
              //     System.out.println("Smjena " + shift + " fali ljudi, ima mjesta: " + staffSchedule.spaceInShift[day][shift]);
                        for(tempDay = day; tempDay < workerSchedule.schedule.length && workerSchedule.schedule[tempDay] != -1 ;tempDay ++) {
                            if ( workerSchedule.schedule[tempDay] < shift ){

                //                System.out.println("****** NAPRIJED");
                //                System.out.println("New shift " + shift);
                //                System.out.println("Old shift " + workerSchedule.schedule[tempDay]);
                                staffSchedule.spaceInShift[tempDay][workerSchedule.schedule[tempDay]] ++;
                //                System.out.println("Povećavam mjesta u smjeni " + workerSchedule.schedule[tempDay]);                                
                                workerSchedule.schedule[tempDay] = shift;
                //                System.out.println("Zamjena " + workerSchedule.schedule[tempDay]);
                                staffSchedule.spaceInShift[tempDay][workerSchedule.schedule[tempDay]] --;
                //                System.out.println("Smanjujem mjesto u smjeni " + workerSchedule.schedule[tempDay]);
                                
                            }else {
                                break;
                            }
                        }
                    }
                }  
            }
            workerSchedule.calculateFitness(worker, instance.numberOfDays);
         //   System.exit(-1);
        }
    }
}
