
package hmo.projekt;

/**
 *
 * @author Marko
 * 
 * 
 */

public class Main {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Instance instance1 = new Instance();
        Algorithm al = new Algorithm(instance1);
        al.getAvailableStaffForEachShift();
        al.getAvailableStaffForEachDay();
        al.getAvailableStaffForEachDayShift();
        al.setDayShiftFlexibility();
        
 //       Main.map.toString();
        
 //       al.toString();
    }
}
