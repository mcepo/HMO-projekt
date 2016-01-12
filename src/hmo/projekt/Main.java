
package hmo.projekt;

import hmo.projekt.structures.Map;
import hmo.projekt.structures.Requests;
import hmo.projekt.structures.Shift;
import hmo.projekt.structures.Staff;
import java.util.LinkedList;

/**
 *
 * @author Marko
 * 
 * 
 */

public class Main {

    
// popis svih radnika sa podacima specifičnim za svakog radnika
    public static LinkedList<Staff> staff = new LinkedList<>();
   
// popis svih smjena sa podacima specifičnim za svaku smjenu
    public static LinkedList<Shift> shift = new LinkedList<>();

// skup svih zahtjeva smjena i radnika
    public static Requests request;

// mapiranje radnika i smjena u njihovim listama shift i staff
// instancira se tek nakon što znamo ukupan broj ljudi i smjena
    public static Map map = new Map();
    
    public static int numberOfDays;
    public static int numberOfShiftsPerDay;
    public static int numberOfStaff;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        new Instance();
        Algorithm al = new Algorithm();
        al.getAvailableStaffForEachShift();
        al.getAvailableStaffForEachDay();
        al.getAvailableStaffForEachDayShift();
        al.setDayShiftFlexibility();
        
 //       Main.map.toString();
        
 //       al.toString();
    }
}
