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
    
    public void balanceDayShifts( StaffSchedule staffSchedule, Instance instance ) { 
        
        staffSchedule.calculateShiftCover(instance.shiftCover);
        
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
                    if ( (day == 0 || workerSchedule.schedule[day -1 ] <= shift) &&
                            (    day == (workerSchedule.schedule.length - 1)
                            || workerSchedule.schedule[day + 1 ] >= shift)
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
        staffSchedule.calculateShiftCover(instance.shiftCover);
    }
}
