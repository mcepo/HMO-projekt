package hmo.projekt;

import hmo.projekt.structures.Map;
import hmo.projekt.structures.Shift;
import hmo.projekt.structures.Staff;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marko
 * 
 * - Predstavlja instance.txt datoteku u programu
 * 
 * - Te sadrži sve potrebne strukture za algoritam koje je moguće inicijalno
 * definirati
 * 
 * 
 */

public class Instance {
    
    public int numberOfDays;
    public int numberOfStaff;
    public int numberOfShiftsPerDay;
 
// ukupan broj smjena numberOfDays * numberOfShiftsPerDay
    public int totalNumberOfShifts;
    
// kolika je "kazna" ako ima previše ili premalo radnika u smjeni
    public int weightForShiftCoverUnder;
    public int weightForShiftCoverOver;
    
    
// koliko je potrebno radnika u pojedinoj smjeni
    public HashMap<Integer, Integer> shiftCover;
    
// popis svih radnika sa podacima specifičnim za svakog radnika
    public List<Staff> staff = new LinkedList<>();
    
// popis svih smjena sa podacima specifičnim za svaku smjenu
    public List<Shift> shift = new LinkedList<>();
    
// mapiranje radnika i smjena u njihovim listama singleShift i singleStaff
// instancira se tek nakon što znamo ukupan broj ljudi i smjena
    public Map map = new Map();

// određena je "fleksibilnost" kod biranja ljudi za pojedinu smjenu
// definira se kao broj ljudi koji ostaje raspoloživ za tu smjenu nakon što se
// odabere potreban broj ljudi
// to je kriterij za odabir koji za koje će se smjene prvo definirati ljudi
// za one smjene s najmanjom fleksibilnosti se prvo bitaju ljudi
//    public List<Integer> dayShiftFlexibility;
    
    enum DataType{
        SECTION_HORIZON, SECTION_SHIFTS, SECTION_STAFF,
        SECTION_DAYS_OFF, SECTION_SHIFT_ON_REQUESTS, 
        SECTION_SHIFT_OFF_REQUESTS, SECTION_COVER;
    }
    
    private BufferedReader br;
    
    public Instance(String filePath) {
        
  //      this.dayShiftFlexibility = new LinkedList<>();
        
        try {
            this.br = new BufferedReader(new FileReader(filePath));
            this.read();
            this.br.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Instance.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Instance.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void read () throws IOException {
    
        String line;
        while ((line = this.br.readLine()) != null) {
            if((line.length() == 0) || (line.substring(0, 1).equals("#"))) { continue; }

            try {
                DataType dataType = DataType.valueOf(line);

                switch(dataType) {
                    case SECTION_HORIZON :
                        this.readSectionHorizon();
                    break;
                    case SECTION_SHIFTS :
                        this.readSectionShifts();
                    break;
                    case SECTION_STAFF :
                        this.readSectionStaff();
                    break;
                    case SECTION_DAYS_OFF :
                        this.readSectionDaysOff();
                    break;
                    case SECTION_SHIFT_ON_REQUESTS :
// ovo inicijaliziram ovdje jer koristim neke podatke koje trebaju biti već
// prethodno učitani npr. ukupan broj ljudi
// NPOMENA: pretpostavljam da SECTION_SHIFT_ON_REQUESTS uvijek ide prije 
// nego SECTION_SHIFT_OFF_REQUESTS
                        this.readSectionShiftOnRequest();
                    break;
                    case SECTION_SHIFT_OFF_REQUESTS :
                        this.readSectionShiftOffRequest();
                    break;
                    case SECTION_COVER :
                        this.readSectionCover();
                    break;
                }
            } catch (IllegalArgumentException ex){
            }
        }
    }
    
    private void readSectionHorizon () throws IOException {
        String line;
        while ((line = this.br.readLine()).length() != 0 ) {
            
            if(line.substring(0, 1).equals("#")) { continue; }
            this.numberOfDays = Integer.parseInt(line);
        }
    }
    private void readSectionShifts() throws IOException {
        String line;
        while ((line = this.br.readLine()).length() != 0 ) {
            
            if(line.substring(0, 1).equals("#")) { continue; }
            Shift singleShift = new Shift(line, this.shift, this.map);

            this.shift.add(singleShift);
            this.numberOfShiftsPerDay = this.shift.size();
            this.totalNumberOfShifts = this.numberOfDays * this.numberOfShiftsPerDay;
        }
    }
    private void readSectionStaff() throws IOException {
        String line;
        while ((line = this.br.readLine()).length() != 0 ) {
            
            if(line.substring(0, 1).equals("#")) { continue; }
            Staff singleStaff = new Staff(line, this.map, this.staff);

            this.staff.add(singleStaff);
        }
        this.numberOfStaff = this.staff.size();
    }
    
    private void readSectionDaysOff() throws IOException {
        String line;
// NAPOMENA: pretpostavljam da su svi radnici navedeni u dijelu SECTION_DAYS_OFF
// i da su istim redom napisani kako i u sekciji SECTION_STAFF
        int i=0;
        while ((line = this.br.readLine()).length() != 0 ) {
            
            if(line.substring(0, 1).equals("#")) { continue; }
            staff.get(i).setDaysOff(line, this.numberOfDays, this.numberOfShiftsPerDay );
            
            i++;
        }
    }
    
    private void readSectionShiftOnRequest() throws IOException {
       String line;
        while ((line = this.br.readLine()).length() != 0 ) {
            
            if(line.substring(0, 1).equals("#")) { continue; }
            
            String[] pieces = line.split(",");
            this.staff.get(this.map.staff.get(pieces[0])).setShiftOnRequest(pieces, this.map, this.numberOfShiftsPerDay);
        }
    }
    
    private void readSectionShiftOffRequest() throws IOException {
        String line;
        while ((line = this.br.readLine()).length() != 0 ) {
            
            if(line.substring(0, 1).equals("#")) { continue; }
                        
            String[] pieces = line.split(",");
            this.staff.get(this.map.staff.get(pieces[0])).setShiftOffRequest(pieces, this.map, this.numberOfShiftsPerDay);
            
  // DEBUG
  //          this.staff.get(this.map.staff.get(pieces[0])).toString();
        }
    }
    
    private void readSectionCover() throws IOException {
        
        String line;
        this.shiftCover = new HashMap<>();
        
        while ((line = this.br.readLine()) != null ) {
            if(line.substring(0, 1).equals("#")) { continue; }
            String[] pieces = line.split(",");
            
            this.shiftCover.put(
                    (Integer.parseInt(pieces[0])*this.numberOfShiftsPerDay+this.map.shift.get(pieces[1])), 
                    Integer.parseInt(pieces[2]) 
            );
            if(this.weightForShiftCoverUnder == 0) {
                this.weightForShiftCoverUnder = Integer.parseInt(pieces[3]);
                this.weightForShiftCoverOver = Integer.parseInt(pieces[4]);
            }
        }
// DEBUG      
//        System.out.println(this.shiftCover.toString());
    }
}
