package hmo.projekt.structures.schedule;

/**
 *
 * @author Marko
 */
public class StaffSchedule  implements Comparable<StaffSchedule>{
    
    public WorkerSchedule[] workerSchedules;
 
    public int[][] shiftCover;
    public Integer totalFitness;
    
    public StaffSchedule ( int numberOfDays, int numberOfShiftsPerDay, int numberOfWorkers ){
        
        this.totalFitness = 0;
        this.workerSchedules = new WorkerSchedule[numberOfWorkers];
        this.shiftCover = new int[numberOfDays][numberOfShiftsPerDay];
    }

// iznačunava fitness za svaku smjenu posebno i ukupni fitness
    public void calculateFitness( int weightForShiftCoverUnder, int weightForShiftCoverOver) {
        
        totalFitness = 0;
        
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
    private void addWorkerFitness() {
        
        for(WorkerSchedule workerSchedule : this.workerSchedules) {
            totalFitness += workerSchedule.fitness;
        }
    }

    @Override
    public int compareTo(StaffSchedule o) {
        return this.totalFitness - o.totalFitness;
    }
}
