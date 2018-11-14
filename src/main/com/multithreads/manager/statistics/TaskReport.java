package com.multithreads.manager.statistics;

/**
 * Report about completed and total tasks.
 */
public class TaskReport {

    private long completed;

    private long total;

    /**
     * Time to do buffer tasks (in nanoseconds).
     */
    private long timeNanoSec;

    /**
     * Initializes completed and total tasks fields.
     *
     * @param completed completed tasks
     * @param total total tasks
     */
    public TaskReport(long completed, long total, long timeNanoSec) {
        this.completed = completed;
        this.total = total;
        this.timeNanoSec = timeNanoSec;
    }

    public long getTimeNanoSec() {
        return timeNanoSec;
    }

    public void setTimeNanoSec(long timeNanoSec) {
        this.timeNanoSec = timeNanoSec;
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

    public void addTotalTasks(long total) {
        this.total += total;
    }

    public void addTotalSpentTime(long total){
        this.total += total;
    }
    @Override
    public String toString() {
        return "TaskReport{" +
                "completed=" + completed +
                ", total=" + total +
                '}';
    }
}
