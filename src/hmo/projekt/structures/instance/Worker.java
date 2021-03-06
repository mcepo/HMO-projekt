package hmo.projekt.structures.instance;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Marko
 */
public class Worker {

    // identifikator radnika
    public String id;
    // smjene koje smije raditi
    public HashSet<Integer> canWorkShift;
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
    public int maxWorkWeekends;
  
    public int minShifts;
    public int maxShifts;
    
// dani u kojima radnik ne smije raditi
    public HashSet<Integer> daysOff ;
    
// želje radnika u kojim smjenama bi htjeli raditi i u kojima nebi htjeli raditi
//                    day     
    public HashMap<Integer, Request> shiftOnRequest;
    public HashMap<Integer, Request> shiftOffRequest;
    
// ovo koristim samo kad inicijaliziram populaciju, koristi se za ravnomjerno
// raspoređivanje radnika kroz sve dane rasporeda
    public List<Integer> spreadDaysOn = new LinkedList<>();
    public List<Integer> spreadDaysOff = new LinkedList<>();
    
    public Worker(String line, HashMap<String, Integer> staffMap, List<Worker> staff, List<Shift> shifts, int numberOfShiftsPerDay) {
        
        this.daysOff = new HashSet<>();
        this.shiftOnRequest = new HashMap<>();
        this.shiftOffRequest = new HashMap<>();
        
        String[] piece = line.split(",");
        
        this.id = piece[0];
       
// mapping       
        staffMap.put(piece[0], staff.size());
        
        this.canWorkShift = new HashSet<>();
        
        String[] workerShift = piece[1].split("[\\|=]");
        int shiftValue;
        for (int shift = 1; shift < workerShift.length; shift+=2){
// NAPOMENA: ovdje pretpostavljaš da su sve smjene navedene i da su poredane 
// istim redom kao i kod sekcije SECTION_SHIFTS
            shiftValue = Integer.parseInt(workerShift[shift]);
            
            if (shiftValue != 0) {
// koja glupost
                this.canWorkShift.add(((shift -1)/2));
            }
        }
        
        this.maxTotalMinutes = Integer.parseInt(piece[2]);
        this.minTotalMinutes = Integer.parseInt(piece[3]);
        this.maxConsecutiveShifts = Integer.parseInt(piece[4]);
        this.minConsecutiveShifts = Integer.parseInt(piece[5]);
        this.minConsecutiveDaysOff = Integer.parseInt(piece[6]);
        this.maxWorkWeekends = Integer.parseInt(piece[7]);
        
        this.maxShifts = (int)(this.maxTotalMinutes/shifts.get(0).lengthMinutes);
        this.minShifts = (int) Math.ceil((double)this.minTotalMinutes/(double)shifts.get(0).lengthMinutes);
    }
    
    public void setDaysOff (String[] pieces, int numberOfDays ) {
        
        for (int index = 1; index < pieces.length ; index ++) {
            this.daysOff.add(Integer.parseInt(pieces[index]));
        }
        this.calculateSpread(numberOfDays);
    }

    public void calculateSpread(int numberOfDays) {
        
        int minDaysOff = numberOfDays - this.daysOff.size() - this.maxShifts;
        int maxDaysOff = numberOfDays - this.daysOff.size() - this.minShifts;
        
        int n;
        
        for (int consecutiveShifts = this.minConsecutiveShifts; consecutiveShifts <= this.maxConsecutiveShifts; consecutiveShifts ++) {
            n = (int) this.maxShifts / consecutiveShifts;
            if ((n*this.minConsecutiveDaysOff) <= minDaysOff) {
                this.spreadDaysOn.add (consecutiveShifts);
                this.spreadDaysOff.add((int) maxDaysOff/n);
            }
        }
    }

    @Override
    public String toString() {
        System.out.println("Worker{" + 
                        "id=" + id + 
                     //   ", maxTotalMinutes=" + maxTotalMinutes + 
                     //   ", minTotalMinutes=" + minTotalMinutes + 
                     //   ", minShifts=" + minShifts + 
                     //   ", maxShifts=" + maxShifts + 
                     //   ", maxConsecutiveShifts=" + maxConsecutiveShifts + 
                     //   ", minConsecutiveShifts=" + minConsecutiveShifts + 
                    //    ", minConsecutiveDaysOff=" + minConsecutiveDaysOff + 
                   //     ", maxWorkWeekends=" + maxWorkWeekends + 
                  //      "\n canWorkShift=" + canWorkShift + 
                        "\n shiftOnRequest=" + shiftOnRequest + 
                        "\n shiftOffRequest=" + shiftOffRequest + 
                   //     "\n daysOff=" + daysOff +
                        "}"
        );
        return "";
    }
}
