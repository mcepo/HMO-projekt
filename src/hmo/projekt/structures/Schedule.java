package hmo.projekt.structures;

import java.util.LinkedList;

/**
 *
 * @author Marko
 * 
 * kod rasporeda osnovna jedinica je smjena
 * tako da ćemo u rasporedu imati brojSmjena*brojDana
 * 
 */
public class Schedule {
 
    // lista kojoj index predstavljaju smjene u pojedinom danu
    // npr. 0 = prva smjena u prvom danu
    //      1 = druga smjena u prvom danu
    //      7 = prva smjena u drugom danu
    //      ....
    // u element liste predstavlja broj radnika koji su potrebni u toj smjeni
    public LinkedList<Integer> sectionCover;
    
    
// NAPOMENA: pretpostavljaš da će težine za previše i premalo ljudi za svaku
// smjenu biti iste
    public int weightForUnder;
    public int weightForOver;
    
    
 //   public LinkedList<Integer[]> schedule;

    public Schedule () {
        this.weightForOver = 0;
        this.weightForUnder = 0;
        this.sectionCover = new LinkedList<>();
        // OVO NE VALJA
//        int totalNumberOfShifts = Main.numberOfDays * Main.numberOfShiftsPerDay;
//        
//        for ( int i = 0; i < totalNumberOfShifts; i++) {
//            this.schedule.add(new Integer[Main.numberOfStaff]);
//        }
    }

    public void setSectionCover(String line) {

        String[] pieces = line.split(",");
        this.sectionCover.add(Integer.parseInt(pieces[2]));
        
        if(this.weightForUnder == 0) {
            this.weightForUnder = Integer.parseInt(pieces[3]);
            this.weightForOver = Integer.parseInt(pieces[4]);
        }
    }

    @Override
    public String toString() {
        System.out.println(
                "Schedule{" + 
                "sectionCover=" + sectionCover + 
                ", weightForUnder=" + weightForUnder + 
                ", weightForOver=" + weightForOver + 
                '}'
        );
        return "";
    }
    
    
}
