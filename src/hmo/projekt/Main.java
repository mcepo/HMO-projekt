

package hmo.projekt;

import hmo.projekt.structures.Schedule;
import hmo.projekt.structures.Shift;
import hmo.projekt.structures.Staff;
import java.util.LinkedList;

/**
 *
 * @author Marko
 */

public class Main {

    public static LinkedList<Staff> staff = new LinkedList<>();
    public static LinkedList<Shift> shift = new LinkedList<>();
    public static Schedule schedule = new Schedule();
    
    public static int numberOfDays;
    public static int numberOfShiftsPerDay;
    public static int numberOfStaff;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        new Instance();
    }
}
