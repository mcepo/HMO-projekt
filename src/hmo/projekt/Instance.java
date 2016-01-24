package hmo.projekt;

import hmo.projekt.structures.instance.Request;
import hmo.projekt.structures.instance.Shift;
import hmo.projekt.structures.instance.Worker;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
    public int numberOfWorkers;
    public int numberOfShiftsPerDay;
    
// kolika je "kazna" ako ima previše ili premalo radnika u smjeni
    public int weightForShiftCoverUnder;
    public int weightForShiftCoverOver;
    
    public int[][] shiftCover;

// popis svih radnika sa podacima specifičnim za svakog radnika
    public List<Worker> staff ;
    
// popis svih smjena sa podacima specifičnim za svaku smjenu
    public List<Shift> shifts ;
    
    public HashSet<Integer> weekendShiftsSaturday;
    public HashSet<Integer> weekendShiftsSunday;
    
// mapiranje radnika i smjena u njihovim listama singleShift i singleStaff
// instancira se tek nakon što znamo ukupan broj ljudi i smjena
    public HashMap<String, Integer> staffMap;
    public HashMap<String, Integer> shiftMap;
    
// radnici poslagani po broju smjena u kojima mogu raditi
    public List<Integer> workersByAllowedNumberOfShifts = new ArrayList<>();
    
    
    
    private enum DataType{
        SECTION_HORIZON, SECTION_SHIFTS, SECTION_STAFF,
        SECTION_DAYS_OFF, SECTION_SHIFT_ON_REQUESTS, 
        SECTION_SHIFT_OFF_REQUESTS, SECTION_COVER;
    }
    
    private BufferedReader br;
    
    public Instance(String filePath) {
        
        this.weekendShiftsSaturday = new HashSet<>();
        this.weekendShiftsSunday = new HashSet<>();
  
        this.staffMap = new HashMap<>();
        this.shiftMap = new HashMap<>();
        
        
        this.staff = new ArrayList<>();
        this.shifts = new ArrayList<>(); 
        
        try {
            this.br = new BufferedReader(new FileReader(filePath));
            this.read();
            this.br.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Instance.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Instance.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.setWeekends();
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
        
//        int max = 0;
//        int min = 0;
//        int cover = 0;
//        for(int i=0;i< this.shiftCover.length;i++){
//            for(int j=0;j<this.shiftCover[i].length;j++){
//                cover += this.shiftCover[i][j];
//            }
//        }
//        
//        
//        for(Worker worker : this.staff){
//            max += worker.maxShifts;
//            min += worker.minShifts;
//        }
//        System.out.println(max + " > " + cover + " > " + min);
//        
//    //    System.exit(0);
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
            
            Shift singleShift = new Shift(line, this.shifts, this.shiftMap);

            this.shifts.add(singleShift);
            this.numberOfShiftsPerDay = this.shifts.size();
        }
    }
    private void readSectionStaff() throws IOException {
        String line;
        while ((line = this.br.readLine()).length() != 0 ) {
            
            if(line.substring(0, 1).equals("#")) { continue; }
            Worker singleStaff = new Worker(line, this.staffMap, this.staff, this.shifts, this.numberOfShiftsPerDay);

            this.staff.add(singleStaff);
        }
        this.numberOfWorkers = this.staff.size();
    }
    
    private void readSectionDaysOff() throws IOException {
        String line;
        while ((line = this.br.readLine()).length() != 0 ) {
            
            if(line.substring(0, 1).equals("#")) { continue; }
            
            String[] pieces = line.split(",");
            staff.get(this.staffMap.get(pieces[0])).setDaysOff(pieces, this.numberOfDays);
        }
    }
    
    private void readSectionShiftOnRequest() throws IOException {
       String line;
        while ((line = this.br.readLine()).length() != 0 ) {
            
            if(line.substring(0, 1).equals("#")) { continue; }
            
            String[] pieces = line.split(",");
            
            this.staff.get(staffMap.get(pieces[0])).shiftOnRequest.put(Integer.parseInt(pieces[1]), 
                    new Request(shiftMap.get(pieces[2]),Integer.parseInt(pieces[3])));
        }
    }
    
    private void readSectionShiftOffRequest() throws IOException {
        String line;
        while ((line = this.br.readLine()).length() != 0 ) {
            
            if(line.substring(0, 1).equals("#")) { continue; }
                        
            String[] pieces = line.split(",");
            
            this.staff.get(staffMap.get(pieces[0])).shiftOffRequest.put(Integer.parseInt(pieces[1]), 
                    new Request(shiftMap.get(pieces[2]),Integer.parseInt(pieces[3])));
        }
    }
    
    private void readSectionCover() throws IOException {
        
        String line;
        
        int day;
        int shift;
        int cover;
        
        this.shiftCover = new int[this.numberOfDays][this.numberOfShiftsPerDay];
        
        while ((line = this.br.readLine()) != null ) {
            if(line.substring(0, 1).equals("#")) { continue; }
            String[] pieces = line.split(",");
            
            day = Integer.parseInt(pieces[0]);
            shift = shiftMap.get(pieces[1]);
            cover = Integer.parseInt(pieces[2]);
            this.weightForShiftCoverUnder = Integer.parseInt(pieces[3]);
            this.weightForShiftCoverOver =Integer.parseInt(pieces[4]);
            
            this.shiftCover[day][shift] = cover;
        }
    }
    
// generiranje doadnih struktura koje će trebati kasnije genetskom algoritmu    
    
    private void setWeekends () {

        for(int day = 5; day < this.numberOfDays; day += 7) {
            this.weekendShiftsSaturday.add(day);
            this.weekendShiftsSunday.add(day+1);
        }
    }
}
