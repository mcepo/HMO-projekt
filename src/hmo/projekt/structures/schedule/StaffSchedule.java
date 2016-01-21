package hmo.projekt.structures.schedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Marko
 */
public class StaffSchedule  implements Comparable<StaffSchedule>{
    
    public List<WorkerSchedule> workerSchedules;
 
    int[][] shiftCover;
    
    public HashMap<Integer, Integer> shiftFitness;
    public Integer totalFitness;
    
    public StaffSchedule ( int[][] shiftCover){
        
        this.totalFitness = 0;
        this.workerSchedules = new ArrayList<>();
        this.shiftFitness = new HashMap<>();
        
        this.shiftCover = new int[shiftCover.length][];
        for(int i = 0; i < shiftCover.length; i++)
        {
            this.shiftCover[i] = new int[shiftCover[i].length];
            System.arraycopy(shiftCover[i], 0, this.shiftCover[i], 0, shiftCover[i].length);
        }
    } 

// iznačunava fitness za svaku smjenu posebno i ukupni fitness
    public void calculateFitness( int weightForShiftCoverUnder, int weightForShiftCoverOver) {
        
        this.calculateShiftCover();
        
        for (int day = 0; day < this.shiftCover.length; day ++) {
            for(int shift=0;shift < this.shiftCover[day].length; shift ++){
    // imam previše ljudi u smjeni
                if (this.shiftCover[day][shift] < 0) {
                    totalFitness += Math.abs(this.shiftCover[day][shift]) * weightForShiftCoverOver;
    // imam premalo ljudi u smjeni
                } else {
                    this.totalFitness += this.shiftCover[day][shift] * weightForShiftCoverUnder;
                }
            }
        }
        
        this.addWorkerFitness();
    }

    public void calculateShiftCover() {
        
        for(WorkerSchedule workerSchedule : this.workerSchedules) {
            for (Map.Entry<Integer, Integer> workShift : workerSchedule.schedule.entrySet()) {
                
                -- this.shiftCover[workShift.getKey()][workShift.getValue()];
            }
        }
    }
    private void addWorkerFitness() {
        
        for(WorkerSchedule workerSchedule : this.workerSchedules) {
            totalFitness += workerSchedule.fitness;
        }
    }

    @Override
    public String toString() {
        
        System.out.println(
         "StaffSchedule{" +
                 "\n shiftCover= " + shiftCover + 
                 "\n shiftFitness= " + shiftFitness + 
                 "\n totalFitness= " + totalFitness + 
                 '}'
        );
        return "";
    }

    @Override
    public int compareTo(StaffSchedule o) {
        return this.totalFitness - o.totalFitness;
    }
}
