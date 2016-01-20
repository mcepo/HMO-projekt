package hmo.projekt;

import hmo.projekt.structures.schedule.StaffSchedule;
import java.util.Comparator;

/**
 *
 * @author Marko
 */
public class CustomComparator implements Comparator<StaffSchedule>{

    @Override
    public int compare(StaffSchedule o1, StaffSchedule o2) {
        return o1.totalFitness.compareTo(o2.totalFitness);
        
    }
}
