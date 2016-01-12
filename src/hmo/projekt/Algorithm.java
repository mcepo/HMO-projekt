package hmo.projekt;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Marko
 */

public class Algorithm {

// koji su sve ljudi raspoloživi u pojedinoj smjeni, neovisno o danu
    public LinkedList<LinkedList<Integer>> availableStaffForEachShift;
    
// koji su ljudi raspoloživi u pojedinom danu, neovisno o smjeni
    public LinkedList<LinkedList<Integer>> availableStaffForEachDay;
    
// koji su ljudi raspoloživi u pojedinoj smjeni u pojedinom danu
// ovaj podataka je dobiven na temelju dozvoljenih smjena iz polja MaxShifts
// i polja SECTION_DAYS_OFF
    public LinkedList<LinkedList<Integer>> availableStaffForEachDayShift;
    
// određena je "fleksibilnost" kod biranja ljudi za pojedinu smjenu
// definira se kao broj ljudi koji ostaje raspoloživ za tu smjenu nakon što se
// odabere potreban broj ljudi
// to je kriterij za odabir koji za koje će se smjene prvo definirati ljudi
// za one smjene s najmanjom fleksibilnosti se prvo bitaju ljudi
    public List<Integer> dayShiftFlexibility;
    
    public Algorithm() {
        this.availableStaffForEachShift = new LinkedList<>();
        this.availableStaffForEachDay = new LinkedList<>();
        this.availableStaffForEachDayShift = new LinkedList<>();
        this.dayShiftFlexibility = new LinkedList<>();
    }

    public void getAvailableStaffForEachShift () {
        
        for( int j=0; j< Main.numberOfShiftsPerDay; j++) {
            LinkedList<Integer> availableStaffForShift = new LinkedList<>();
            for(int i=0;i< Main.numberOfStaff;i++) {
                if(Main.staff.get(i).maxShifts.get(j) == 0 ) { continue; }
                availableStaffForShift.add(i);
            }
            this.availableStaffForEachShift.add(availableStaffForShift);
        }
    }
    
    public void getAvailableStaffForEachDay () {
        
        for( int j=0; j< Main.numberOfDays; j++) {
            LinkedList<Integer> availableStaffForDay = new LinkedList<>();
            for(int i=0;i< Main.numberOfStaff;i++) {
                if(Main.staff.get(i).daysOff.contains(j)) { continue ; }
                availableStaffForDay.add(i);
            }
            this.availableStaffForEachDay.add(availableStaffForDay);
        }
    }
    
    public void getAvailableStaffForEachDayShift() {
        
        for(int i=0 ; i< Main.numberOfDays; i++) {
            for (int j=0;j< Main.numberOfShiftsPerDay;j++){
                LinkedList<Integer> common = new LinkedList<>(this.availableStaffForEachDay.get(i));
                common.retainAll(this.availableStaffForEachShift.get(j));
                this.availableStaffForEachDayShift.add(common);
            }
        }
    }
    
    public void setDayShiftFlexibility(){
        for(int i=0 ;i<this.availableStaffForEachDayShift.size();i++){
           
            this.dayShiftFlexibility.add(
                    this.availableStaffForEachDayShift.get(i).size() - 
                    Main.request.shiftCover.get(i)
            );
        } 
    }

    @Override
    public String toString() {
//        System.out.println("*** Raspoloživi radnici po smjenama ***");
//        this.availableStaffForEachShift.stream().forEach((as) -> {
//            System.out.println( as );
//        });
//        
//        System.out.println("*** Raspoloživi radnici po danima ***");
//        this.availableStaffForEachDay.stream().forEach((as) -> {
//            System.out.println( as );
//        });
//        System.out.println("*** Raspoloživi radnici za svaku smjenu u svakom danu ***");
//        this.availableStaffForEachDayShift.stream().forEach((as) -> {
//            System.out.println( as );
//        });
        System.out.println("*** Sloboda pojedine smjene ***");
        Collections.sort(this.dayShiftFlexibility);
        this.dayShiftFlexibility.stream().forEach((as) -> {
            System.out.println( as );
        });
        return "";
    }
}

