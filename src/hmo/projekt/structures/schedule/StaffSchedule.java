package hmo.projekt.structures.schedule;

/**
 *
 * @author Marko
 */
public class StaffSchedule  implements Comparable<StaffSchedule>{
    
    public WorkerSchedule[] workerSchedules;
 
    public int[][] shiftCover;
    public Integer fitness;
    public int shiftFitness;
    public int staffFitness;
    
    public StaffSchedule ( int numberOfDays, int numberOfShiftsPerDay, int numberOfWorkers ){
        
        this.fitness = 0;
        this.shiftFitness = 0;
        this.staffFitness = 0;
        this.workerSchedules = new WorkerSchedule[numberOfWorkers];
        this.shiftCover = new int[numberOfDays][numberOfShiftsPerDay];
    }
    
    public void calculateFitness(int weightForShiftCoverUnder, int weightForShiftCoverOver){
        
        this.fitness = this.calculateShiftFitness(weightForShiftCoverUnder, weightForShiftCoverOver) + this.calculateStaffFitness();  
    }
    
// iznačunava fitness za svaku smjenu posebno i ukupni fitness
    public int calculateShiftFitness( int weightForShiftCoverUnder, int weightForShiftCoverOver) {
        
        this.shiftFitness = 0;
        
        for (int day = 0; day < this.shiftCover.length; day ++) {
            for(int shift=0;shift < this.shiftCover[day].length; shift ++){
    // imam previše ljudi u smjeni
                if (this.shiftCover[day][shift] < 0) {
                    this.shiftFitness += Math.abs(this.shiftCover[day][shift]) * weightForShiftCoverOver;
    // imam premalo ljudi u smjeni
                } else {
                    this.shiftFitness += this.shiftCover[day][shift] * weightForShiftCoverUnder;
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
        
        for(int day = 0;day < shiftCover.length;day ++) {
            for(int shift = 0 ; shift < shiftCover[day].length; shift ++){
                this.shiftCover[day][shift] = shiftCover[day][shift];
            }
        }

        for(WorkerSchedule workerSchedule : this.workerSchedules) {
            for(int day = 0; day < workerSchedule.schedule.length; day ++) {
                
                if(workerSchedule.schedule[day] != -1) {
                    -- this.shiftCover[day][workerSchedule.schedule[day]];
                }
            }
        }
    }

    @Override
    public int compareTo(StaffSchedule o) {
        return this.fitness - o.fitness;
    }
}
