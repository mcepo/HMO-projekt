package hmo.projekt;

import hmo.projekt.structures.Map;
import hmo.projekt.structures.Shift;
import hmo.projekt.structures.Staff;
import hmo.projekt.structures.Requests;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
// skup svih zahtjeva smjena i radnika
    public Requests request;
    
// popis svih radnika sa podacima specifičnim za svakog radnika
    public LinkedList<Staff> staff = new LinkedList<>();
    
// popis svih smjena sa podacima specifičnim za svaku smjenu
    public LinkedList<Shift> shift = new LinkedList<>();
    
// mapiranje radnika i smjena u njihovim listama singleShift i singleStaff
// instancira se tek nakon što znamo ukupan broj ljudi i smjena
    public Map map = new Map();
    
// koji su sve ljudi raspoloživi u pojedinoj smjeni, neovisno o danu
// jer prema zahtjevima zadatka ne mogu svi ljudi raditi u svim smjenama
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
    
    enum DataType{
        SECTION_HORIZON, SECTION_SHIFTS, SECTION_STAFF,
        SECTION_DAYS_OFF, SECTION_SHIFT_ON_REQUESTS, 
        SECTION_SHIFT_OFF_REQUESTS, SECTION_COVER;
    }
    
    private BufferedReader br;
    
    public Instance(String filePath) {
        
        this.availableStaffForEachShift = new LinkedList<>();
        this.availableStaffForEachDay = new LinkedList<>();
        this.availableStaffForEachDayShift = new LinkedList<>();
        this.dayShiftFlexibility = new LinkedList<>();
        
        try {
            this.br = new BufferedReader(new FileReader(filePath));
            this.read();
            this.br.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Instance.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Instance.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.getAvailableStaffForEachShift();
        this.getAvailableStaffForEachDay();
        this.getAvailableStaffForEachDayShift();
        this.setDayShiftFlexibility();
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
                        this.request = new Requests(this.numberOfStaff);
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
        }
    }
    private void readSectionStaff() throws IOException {
        String line;
        while ((line = this.br.readLine()).length() != 0 ) {
            
            if(line.substring(0, 1).equals("#")) { continue; }
            Staff singleStaff = new Staff(line, this.map, this.staff);
// DEBUG
//           singleStaff.toString();
            this.staff.add(singleStaff);
            numberOfStaff = this.staff.size();
        }
    }
    
    private void readSectionDaysOff() throws IOException {
        String line;
// NAPOMENA: pretpostavljam da su svi radnici navedeni u dijelu SECTION_DAYS_OFF
// i da su istim redom napisani kako i u sekciji SECTION_STAFF
        int i=0;
        while ((line = this.br.readLine()).length() != 0 ) {
            
            if(line.substring(0, 1).equals("#")) { continue; }
            staff.get(i).setDaysOff(line);
// DEBUG
 //           Main.singleStaff.get(i).toString();
            i++;
        }
    }
    
    private void readSectionShiftOnRequest() throws IOException {
       String line;
        while ((line = this.br.readLine()).length() != 0 ) {
            
            if(line.substring(0, 1).equals("#")) { continue; }
            request.setStaffShiftRequests(line, true, this.map, this.numberOfShiftsPerDay );
        }
// DEBUG
//     Main.request.toString();
    }
    
    private void readSectionShiftOffRequest() throws IOException {
        String line;
        while ((line = this.br.readLine()).length() != 0 ) {
            
            if(line.substring(0, 1).equals("#")) { continue; }
            request.setStaffShiftRequests(line, false, this.map, this.numberOfShiftsPerDay );
        }
// DEBUG
     //   request.toString();
    }
    
    private void readSectionCover() throws IOException {
        String line;
        while ((line = this.br.readLine()) != null ) {
            if(line.substring(0, 1).equals("#")) { continue; }
            request.setSectionCover(line);
        }
// DEBUG
     //   request.toString();
    }
    
    public void getAvailableStaffForEachShift () {
        
        for( int j=0; j< this.numberOfShiftsPerDay; j++) {
            LinkedList<Integer> availableStaffForShift = new LinkedList<>();
            for(int i=0;i< this.numberOfStaff;i++) {
                if(this.staff.get(i).maxShifts.get(j) == 0 ) { continue; }
                availableStaffForShift.add(i);
            }
            this.availableStaffForEachShift.add(availableStaffForShift);
        }
    }
    
    public void getAvailableStaffForEachDay () {
        
        for( int j=0; j< this.numberOfDays; j++) {
            LinkedList<Integer> availableStaffForDay = new LinkedList<>();
            for(int i=0;i< this.numberOfStaff;i++) {
                if(this.staff.get(i).daysOff.contains(j)) { continue ; }
                availableStaffForDay.add(i);
            }
            this.availableStaffForEachDay.add(availableStaffForDay);
        }
    }
    
    public void getAvailableStaffForEachDayShift() {
        
        for(int i=0 ; i< this.numberOfDays; i++) {
            for (int j=0;j< this.numberOfShiftsPerDay;j++){
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
                    this.request.shiftCover.get(i)
            );
        } 
    }
}
