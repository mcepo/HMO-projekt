package hmo.projekt.structures.schedule;

import hmo.projekt.Instance;

/**
 *
 * @author Marko
 */
public class StaffSchedule  implements Comparable<StaffSchedule>{
    
    public WorkerSchedule[] workerSchedules;
 
    public int[][] spaceInShift;
    public Integer fitness;
    public int shiftFitness;
    public int staffFitness;
    
    public StaffSchedule ( Instance instance ){
        
        this.fitness = 0;
        this.shiftFitness = 0;
        this.staffFitness = 0;
        this.workerSchedules = new WorkerSchedule[instance.numberOfWorkers];
        this.spaceInShift = new int[instance.numberOfDays][instance.numberOfShiftsPerDay];
    }
    
    public void calculateFitness(Instance instance){
        
        this.fitness = this.calculateShiftFitness(instance.weightForShiftCoverUnder, instance.weightForShiftCoverOver) + this.calculateStaffFitness();  
    }
    
// iznačunava fitness za svaku smjenu posebno i ukupni fitness
    public int calculateShiftFitness( int weightForShiftCoverUnder, int weightForShiftCoverOver) {
        
        this.shiftFitness = 0;
        
        for (int day = 0; day < this.spaceInShift.length; day ++) {
            for(int shift=0;shift < this.spaceInShift[day].length; shift ++){
                if (this.spaceInShift[day][shift] < 0) {
                // imam previše ljudi u smjeni
                    this.shiftFitness += (Math.abs(this.spaceInShift[day][shift]) * weightForShiftCoverOver);
                } else if (this.spaceInShift[day][shift] > 0){
                // imam premalo ljudi u smjeni
                    this.shiftFitness += (this.spaceInShift[day][shift] * weightForShiftCoverUnder);
                }
            }
        }

        return this.shiftFitness;
    }
 
    private int calculateStaffFitness() {
        
        this.staffFitness = 0;
        
        for(WorkerSchedule workerSchedule : this.workerSchedules) {
            staffFitness += workerSchedule.fitness;
        }
        return this.staffFitness;
    }
    
    public void calculateShiftCover(int[][] shiftCover) {

        for(int day = 0;day < spaceInShift.length;day ++) {
            System.arraycopy(shiftCover[day], 0, this.spaceInShift[day], 0, shiftCover[day].length);
        }

        for(WorkerSchedule workerSchedule : this.workerSchedules) {
            for(int day = 0; day < workerSchedule.schedule.length; day ++) {
                
                if(workerSchedule.schedule[day] != -1) {
                    this.spaceInShift[day][workerSchedule.schedule[day]] --;
                }
            }
        }
    }

    @Override
    public int compareTo(StaffSchedule o) {
        return this.fitness - o.fitness;
    }
}
