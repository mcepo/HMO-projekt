package hmo.projekt.structures;

import java.util.LinkedList;

/**
 *
 * @author Marko
 */
public class Staff {

    // identifikator radnika
    public String id;
    // maksimalan broj smjena koje smije imati
    public LinkedList<Integer> maxShifts;
    // maximalan broj minuta koje radnik smije odraditi
    public int maxTotalMinutes;
    // minimalan broj minuta koje radnik smije iodraditi
    public int minTotalMinutes;
    // maksimalan broj uzastopnih smjena koje radnik smije imati
    public int maxConsecutiveShifts;
    // minimalan broj uzastopnih smjena koje radnik smije imati
    public int minConsecutiveShifts;
    // minimalan broj uzastopnih dana koje radnik mora biti slobodan
    public int minConsecutiveDaysOff;
    // maksimalan broj vikenda koje radnik može provesti radeći        
    public int maxWeekends;
    // slobdni dani
    public LinkedList<Integer> daysOff;
    // 

    public Staff(String line) {
        String[] piece = line.split(",");
        
        this.id = piece[0];
       
        this.maxShifts = new LinkedList<>();
        
        String[] workerShift = piece[1].split("[\\|=]");
        
        for (int i = 1; i < workerShift.length; i+=2){
// NAPOMENA: ovdje pretpostavljaš da su sve smjene navedene i da su poredane 
// istim redom kao i kod sekcije SECTION_SHIFTS
            this.maxShifts.add(Integer.parseInt(workerShift[i]));
        }
        
        this.maxTotalMinutes = Integer.parseInt(piece[2]);
        this.minTotalMinutes = Integer.parseInt(piece[3]);
        this.maxConsecutiveShifts = Integer.parseInt(piece[4]);
        this.minConsecutiveShifts = Integer.parseInt(piece[5]);
        this.minConsecutiveDaysOff = Integer.parseInt(piece[6]);
        this.maxWeekends = Integer.parseInt(piece[7]);
    }
    
    public void setDaysOff (String line) {

        String[] pieces = line.split(",");
        
        this.daysOff = new LinkedList<>();
        for (int i = 1; i < pieces.length; i++ ) {
            
            this.daysOff.add(Integer.parseInt(pieces[i]));
        }
    }
    

    @Override
    public String toString() {
        System.out.println(
                "Staff{" + 
                "id=" + id + 
                ", maxShifts=" + maxShifts + 
                ", maxTotalMinutes=" + maxTotalMinutes + 
                ", minTotalMinutes=" + minTotalMinutes + 
                ", maxConsecutiveShifts=" + maxConsecutiveShifts + 
                ", minConsecutiveShifts=" + minConsecutiveShifts + 
                ", minConsecutiveDaysOff=" + minConsecutiveDaysOff + 
                ", maxWeekends=" + maxWeekends + 
                ", daysOff=" + daysOff + 
                '}'
        );
        return "";
    }
    
    
}
