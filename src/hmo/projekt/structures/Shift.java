package hmo.projekt.structures;

import hmo.projekt.Main;
import java.util.LinkedList;

/**
 *
 * @author Marko
 */
public class Shift {

    // identifikator smjene
    public String id;
    // trajanje smjene u minutama
    public int lengthMinutes;
    // spremam index iz liste smjene na koje mjestu se nalazi smjena koja ne smije
    // slijediti ovu smjenu
    public LinkedList<Integer> cantFollowShift;
    
    public Shift(String line) {
        String[] piece = line.split(",");
        
        this.id = piece[0];
        
// mapping
        Main.map.shift.put(piece[0], Main.shift.size());
        this.lengthMinutes = Integer.parseInt(piece[1]);
       
        if(piece.length > 2 && piece[2] != null) {
            this.cantFollowShift = new LinkedList<>();
            String[] shiftCantFollow = piece[2].split("\\|");
            for (int i = 0; i < shiftCantFollow.length; i++){
/* NAPOMENA: ovdje pretpostavljaš da su smjene koju su navedene u varijabli shiftCantFollow
                već postavljene u listu svih smjena
Spremam redni broj smjene u listi, jer mislim da će mi tako poslije biti jednostavnije
za raditi
*/
		for (int j = 0; j < Main.shift.size(); j++) {
                    if(Main.shift.get(j).id.equals(shiftCantFollow[i])){
                        this.cantFollowShift.add(j);
                        break;
                    }
		}
            }   
        }
    }

    @Override
    public String toString() {
        System.out.println( "Shift{" + "id=" + id + 
                            ", lengthMinutes=" + lengthMinutes + 
                            ", cantFollowShift=" + cantFollowShift + 
                '}');
        return null;
    }
}
