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
            
            for(int day=0; day < workerSchedule.schedule.length; day++ ) {
                if (workerSchedule.schedule[day] != -1){
                    System.out.print(instance.shifts.get(workerSchedule.schedule[day]).id + "\t");
                } else {
                    System.out.print("  \t");
                }
            }
            System.out.println();
        }
    }
    
    public static void scheduleToFile(StaffSchedule schedule, Instance instance, String filename) {
        
        PrintWriter writer;
        try {
            writer = new PrintWriter(filename, "UTF-8");
            for(WorkerSchedule workerSchedule : schedule.workerSchedules) {
            
            for(int day=0; day < workerSchedule.schedule.length; day++ ) {
                if (workerSchedule.schedule[day] != -1){
                    writer.print(instance.shifts.get(workerSchedule.schedule[day]).id + "\t");
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
    
    public static void workerSchedule(int[] schedule, Instance instance, int workerId){
        
         System.out.println(" ************************* Raspored " + instance.staff.get(workerId).id+ "***************************");
        for(int day = 0; day < schedule.length;day ++) {
            if (schedule[day] != -1) {
                System.out.print(day + " " + schedule[day]);
            } else {
                System.out.print(day + " Ne radim!");
            }
            
            if(instance.weekendShiftsSaturday.contains(day)) {
                System.out.println( "\t\tSubota");
            } else if (instance.weekendShiftsSunday.contains(day)) {
                System.out.println( "\t\tNedjelja");
            } else {
                System.out.println();
            }
        }
    }
    
    public static void twoDimenzionalArray(int[][] array){
        System.out.print("Array { ");
        for(int i = 0;i< array.length;i++){
            System.out.print(" [ ");
            for(int j=0;j<array[i].length;j++){
                System.out.print(" ," + array[i][j]);
            }
            
            System.out.print(" ] ");
        }
        System.out.println();
    }
}
