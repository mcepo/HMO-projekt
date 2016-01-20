package hmo.projekt;

import hmo.projekt.structures.schedule.StaffSchedule;
import hmo.projekt.structures.schedule.WorkerSchedule;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marko
 */
public class PrettyPrint {
    
    public static void scheduleToStdout(StaffSchedule schedule, Instance instance) {
        
        for(WorkerSchedule workerSchedule : schedule.workerSchedules) {
            
            for(int day=0; day < instance.numberOfDays; day++ ) {
                if (workerSchedule.schedule.containsKey(day)){
                    System.out.print(instance.shifts.get(workerSchedule.schedule.get(day)).id + "\t");
                } else {
                    System.out.print("  \t");
                }
            }
            System.out.println();
        }
    }
    
    public static void scheduleToFile(StaffSchedule schedule, Instance instance) {
        
        PrintWriter writer;
        try {
            writer = new PrintWriter("result.txt", "UTF-8");
            for(WorkerSchedule workerSchedule : schedule.workerSchedules) {
            
            for(int day=0; day < instance.numberOfDays; day++ ) {
                if (workerSchedule.schedule.containsKey(day)){
                    writer.print(instance.shifts.get(workerSchedule.schedule.get(day)).id + "\t");
                } else {
                    writer.print("  \t");
                }
            }
            writer.println();
        }
        writer.close();
        
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(PrettyPrint.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
