package hmo.projekt.structures.instance;

import java.util.HashMap;

/**
 *
 * @author Marko
 * 
 * Mapira mi id korisnika prema njegovom mjestu u listi staff
 * isto tako mi mapira id shift sa mjestom u listi shift
 * 
 */
public class Map {
    
    public HashMap<String, Integer> staff;
    public HashMap<String, Integer> shift;
    
    public Map(){
        this.shift = new HashMap<>();
        this.staff = new HashMap<>();
    }

    @Override
    public String toString() {
        System.out.println(
                "Map{\n" + 
                "staff=" + staff + 
                "\nshift=" + shift + "\n}"
        );
        return "";
    }
}
