package hmo.projekt.structures.schedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Marko
 */
public class StaffSchedule {
    
    public List<WorkerSchedule> workerSchedules;

// ovo ću vjerovatno koristiti kod mutacija
    public HashMap<Integer, Integer> shiftCover;
    
    public HashMap<Integer, Integer> shiftFitness;
    public Integer totalFitness;
    
    public StaffSchedule (HashMap<Integer, Integer> shiftCover){
        
        this.totalFitness = 0;
        this.workerSchedules = new ArrayList<>();
        this.shiftCover = new HashMap<>(shiftCover);
        this.shiftFitness = new HashMap<>();
    } 

// iznačunava fitness za svaku smjenu posebno i ukupni fitness
    public void calculateShiftFitness( int weightForCoverUnder, int weightForCoverOver) {
        
        for (Integer numberOfWorkers : this.shiftCover.values()) {
// imam previše ljudi u smjeni
            if (numberOfWorkers < 0) {
                totalFitness += Math.abs(numberOfWorkers) * weightForCoverOver;
// imam premalo ljudi u smjeni
            } else {
                this.totalFitness += numberOfWorkers * weightForCoverUnder;
            }
        }
    }
}
