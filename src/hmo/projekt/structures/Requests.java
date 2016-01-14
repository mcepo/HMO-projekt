package hmo.projekt.structures;

import java.util.LinkedList;

/**
 *
 * @author Marko
 * 
 * Struktura koja sadrži "globalne" zahtjeve pojedine smjene(broj radnika)
 * i zahtjeve radnika (želi/ne želi raditi u pojedinoj smjeni)
 * 
 * 
 */
public class Requests {
 
// u element liste predstavlja broj radnika koji su potrebni u toj smjeni
    public LinkedList<Integer> shiftCover;

// ova varijabla predstavlja koliko je poželjno da radnik radi u pojedinoj smjeni
// što je manja vrijednost polja, to je poželjnije za radnika da radi u toj smjeni
// vrijednosti mogu biti negativne
// jedina struktura kod koje je zbog praktičnosti stavljeno obrnuto, vanjska lista
// predstavlja ljude, a unutarnja smjene po danima
    public LinkedList<LinkedList<Integer>> staffOnShift;
    
    
// NAPOMENA: pretpostavljaš da će težine za previše i premalo ljudi za svaku
// smjenu biti iste
    public int weightForUnderCover;
    public int weightForOverCover;

    public Requests ( int numberOfStaff ) {
        this.weightForOverCover = 0;
        this.weightForUnderCover = 0;
        this.shiftCover = new LinkedList<>();
        this.staffOnShift = new LinkedList<>();
        
        for (int i = 0; i < numberOfStaff;i++){
            LinkedList<Integer> column = new LinkedList<>();
            this.staffOnShift.add(column);
        }
    }

    public void setSectionCover(String line) {

        String[] pieces = line.split(",");
        this.shiftCover.add(Integer.parseInt(pieces[2]));
        
        if(this.weightForUnderCover == 0) {
            this.weightForUnderCover = Integer.parseInt(pieces[3]);
            this.weightForOverCover = Integer.parseInt(pieces[4]);
        }
    }
// NAPOMENA: pretpostavljam da svaki radnik u instanca.txt ima neki zahtjev
//  u kojoj smjeni bi htjeo raditi
    public void setStaffShiftRequests(String line, boolean advisable, Map map, Integer numberOfShiftsPerDay ) {

        String[] piece = line.split(",");
        
        int staffId = map.staff.get(piece[0]);
        int day = Integer.parseInt(piece[1]);
        int shift = map.shift.get(piece[2]);
        int weight = Integer.parseInt(piece[3]);
        int dayShift = day * numberOfShiftsPerDay + shift ;

        for (int i=this.staffOnShift.get(staffId).size(); i< dayShift;i++){
            this.staffOnShift.get(staffId).add(0);
        }
        if (advisable) {
            this.staffOnShift.get(staffId).add(-weight);
        } else {
            this.staffOnShift.get(staffId).add(weight);
        }
        
    }

    @Override
    public String toString() {
        System.out.println(
                "Schedule{" + 
                "\n sectionCover=" + shiftCover + 
                "\n weightForUnder=" + weightForUnderCover + 
                "\n weightForOver=" + weightForOverCover + 
                "\n ***** Zahtjevi svih ljudi po svim smjenama, što je manji broj to bolje ***** " +
                "\n staffOnShift=" + staffOnShift + 
                "\n}"
        );
        return "";
    }
}
