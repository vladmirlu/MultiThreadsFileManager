package com.multithreads.statistic;

/**
 * Report about completed and total tasks.
 */
public class TaskReport {

    private long completed;

    private long total;

    /**
     * Time to do buffer tasks (in nanoseconds).
     */
    private long spentTimeNanoSec;

    /**
     * Initializes completed and total tasks fields.
     *
     * @param completed completed tasks
     * @param total total tasks
     */
    public TaskReport(long completed, long total, long spentTimeNanoSec) {
        this.completed = completed;
        this.total = total;
        this.spentTimeNanoSec = spentTimeNanoSec;
    }

    public long getSpentTimeNanoSec() {
        return spentTimeNanoSec;
    }

    public long getCompleted() {
        return completed;
    }

    public long getTotal() {
        return total;
    }

    public void setCompleted(long completed) {
        this.completed = completed;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public void addCompletedTasks(long completed) {
        this.completed += completed;
    }

    public void addTotalSpentTime(long  spentTimeNanoSec){
        this. spentTimeNanoSec +=  spentTimeNanoSec;
    }
}
