package hmo.projekt.structures;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;



/**
 *
 * @author Marko
 */
public class Staff {

    // identifikator radnika
    public String id;
    // maksimalan broj smjena koje smije imati
    public HashMap<Integer, Integer> canWorkShift;
    // maximalan broj minuta koje radnik smije odraditi
    public int maxTotalMinutes;
    // minimalan broj minuta koje radnik smije odraditi
    public int minTotalMinutes;
    // maksimalan broj uzastopnih smjena koje radnik smije imati
    public int maxConsecutiveShifts;
    // minimalan broj uzastopnih smjena koje radnik smije imati
    public int minConsecutiveShifts;
    // minimalan broj uzastopnih dana koje radnik mora biti slobodan
    public int minConsecutiveDaysOff;
    // maksimalan broj vikenda koje radnik može provesti radeći        
    public int maxWeekends;
  
// popis smjena u svim danima u kojima radnik može raditi s obzirom na 
// slobodne dane i smjene u kojima ne smije raditi
    public HashSet<Integer> canWorkEachDayShift;
    
    public HashMap<Integer, Integer> shiftOnRequest;
    public HashMap<Integer, Integer> shiftOffRequest;
    // 

    public Staff(String line, Map map, List<Staff> staff) {
        
        this.shiftOnRequest = new HashMap<>();
        this.shiftOffRequest = new HashMap<>();
        
        
        String[] piece = line.split(",");
        
        this.id = piece[0];
       
// mapping       
        map.staff.put(piece[0], staff.size());
        
        this.canWorkShift = new HashMap<>();
        
        String[] workerShift = piece[1].split("[\\|=]");
        int shiftValue;
        for (int shift = 1; shift < workerShift.length; shift+=2){
// NAPOMENA: ovdje pretpostavljaš da su sve smjene navedene i da su poredane 
// istim redom kao i kod sekcije SECTION_SHIFTS
            shiftValue = Integer.parseInt(workerShift[shift]);
            
            if (shiftValue != 0) {
// koja glupost
                this.canWorkShift.put(((shift -1)/2), shiftValue);
            }
        }
        
        this.maxTotalMinutes = Integer.parseInt(piece[2]);
        this.minTotalMinutes = Integer.parseInt(piece[3]);
        this.maxConsecutiveShifts = Integer.parseInt(piece[4]);
        this.minConsecutiveShifts = Integer.parseInt(piece[5]);
        this.minConsecutiveDaysOff = Integer.parseInt(piece[6]);
        this.maxWeekends = Integer.parseInt(piece[7]);
    }
    
    public void setDaysOff (String line, int numberOfDays, int numberOfShiftsPerDay) {

        String[] pieces = line.split(",");
        
//        this.daysOff = new LinkedList<>();
        this.canWorkEachDayShift = new HashSet<>();
        
// inicijaliziraj
        for (int i=0;i< numberOfDays * numberOfShiftsPerDay; i++){
            this.canWorkEachDayShift.add(i);
        }
        int day;

// ukloni dane godišnjeg odmora
        for (int i = 1; i < pieces.length; i++ ) {
            
            day = Integer.parseInt(pieces[i]);
            
            for(int shift=0;shift<numberOfShiftsPerDay;shift++) {
                this.canWorkEachDayShift.remove(day*numberOfShiftsPerDay+shift);
            }
        }
        
// ukloni smjene u kojima ne smije raditi
        for(Integer shift : this.canWorkShift.keySet()) {
            for( day = 0; day < numberOfDays; day ++) {
                this.canWorkEachDayShift.remove((day*numberOfShiftsPerDay) + shift);
            }
        }
    }

// postavi koje smjene radnik preferira i kolika je kazna ako ne radi toj smjeni
    public void setShiftOnRequest(String[] pieces, Map map, int numberOfShiftsPerDay) {
        
        int day = Integer.parseInt(pieces[1]);
        int shift = map.shift.get(pieces[2]);
        int weight = Integer.parseInt(pieces[3]);
 //       System.out.println("Day " + day + " shift " + shift + " weight " + weight + " (day * numberOfShiftsPerDay + shift) " + (day * numberOfShiftsPerDay + shift));
        this.shiftOnRequest.put((day * numberOfShiftsPerDay + shift), weight);
        
    }

// postavi smjene koje radnik ne preferira i kolika je kazna ako radi u toj smjeni
    public void setShiftOffRequest(String[] pieces, Map map, int numberOfShiftsPerDay) {
        
        int day = Integer.parseInt(pieces[1]);
        int shift = map.shift.get(pieces[2]);
        int weight = Integer.parseInt(pieces[3]);
        this.shiftOffRequest.put((day * numberOfShiftsPerDay + shift), weight);
        
    }
    

    @Override
    public String toString() {
        System.out.println(
                "Staff{" + 
                        "id=" + id + 
                        ", canWorkShift=" + canWorkShift + 
                        ", maxTotalMinutes=" + maxTotalMinutes + 
                        ", minTotalMinutes=" + minTotalMinutes + 
                        ", maxConsecutiveShifts=" + maxConsecutiveShifts + 
                        ", minConsecutiveShifts=" + minConsecutiveShifts + 
                        ", minConsecutiveDaysOff=" + minConsecutiveDaysOff + 
                        ", maxWeekends=" + maxWeekends + 
                        "\n canWorkEachDayShift=" + canWorkEachDayShift + 
                        "\n shiftOnRequest=" + shiftOnRequest + 
                        "\n shiftOffRequest=" + shiftOffRequest + 
                        "}"
        );
        return "";
    }
}
