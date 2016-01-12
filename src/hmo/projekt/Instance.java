package hmo.projekt;

import hmo.projekt.structures.Shift;
import hmo.projekt.structures.Staff;
import hmo.projekt.structures.Requests;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marko
 * 
 * Metode za čitanje instance.txt
 * te postavljanje pročitanih vrijednosti u zadane strukture
 * 
 */

public class Instance {
    
    enum DataType{
        SECTION_HORIZON, SECTION_SHIFTS, SECTION_STAFF,
        SECTION_DAYS_OFF, SECTION_SHIFT_ON_REQUESTS, 
        SECTION_SHIFT_OFF_REQUESTS, SECTION_COVER;
    }
    
    private BufferedReader br;
    
    public Instance() {
        
        try {
            this.br = new BufferedReader(new FileReader(Local.instanceFilePath));
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
                        Main.request = new Requests();
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
            
            Main.numberOfDays = Integer.parseInt(line);
        }
    }
    private void readSectionShifts() throws IOException {
        String line;
        while ((line = this.br.readLine()).length() != 0 ) {
            
            if(line.substring(0, 1).equals("#")) { continue; }
            
            Shift shift = new Shift(line);
// DEBUG
//            shift.toString();
            Main.shift.add(shift);
            Main.numberOfShiftsPerDay = Main.shift.size();
        }
    }
    private void readSectionStaff() throws IOException {
        String line;
        while ((line = this.br.readLine()).length() != 0 ) {
            
            if(line.substring(0, 1).equals("#")) { continue; }
            
            Staff staff = new Staff(line);
// DEBUG
//            staff.toString();
            Main.staff.add(staff);
            Main.numberOfStaff = Main.staff.size();
        }
    }
    
    private void readSectionDaysOff() throws IOException {
        String line;
// NAPOMENA: pretpostavljam da su svi radnici navedeni u dijelu SECTION_DAYS_OFF
// i da su istim redom napisani kako i u sekciji SECTION_STAFF
        int i=0;
        while ((line = this.br.readLine()).length() != 0 ) {
            
            if(line.substring(0, 1).equals("#")) { continue; }
            
            Main.staff.get(i).setDaysOff(line);
// DEBUG
 //           Main.staff.get(i).toString();
            i++;
        }
    }
    
    private void readSectionShiftOnRequest() throws IOException {
       String line;
        while ((line = this.br.readLine()).length() != 0 ) {
            
            if(line.substring(0, 1).equals("#")) { continue; }
            
            Main.request.setStaffShiftRequests(line, true);
        }
// DEBUG
//     Main.request.toString();
    }
    
    private void readSectionShiftOffRequest() throws IOException {
        String line;
        while ((line = this.br.readLine()).length() != 0 ) {
            
            if(line.substring(0, 1).equals("#")) { continue; }
            
            Main.request.setStaffShiftRequests(line, false);
        }
// DEBUG
     Main.request.toString();
    }
    
    private void readSectionCover() throws IOException {
        String line;
        while ((line = this.br.readLine()) != null ) {
            if(line.substring(0, 1).equals("#")) { continue; }
            Main.request.setSectionCover(line);
        }
// DEBUG
     Main.request.toString();
    }
}
