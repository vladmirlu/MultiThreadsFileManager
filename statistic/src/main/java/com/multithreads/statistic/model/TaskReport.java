package com.multithreads.statistic.model;

/**
 * Report about completed and total tasks and spent time.
 */
public class TaskReport {

    /**
     * Quantity of copied bytes
     */
    private volatile long completed;

    /**
     * Total bytes quantity
     */
    private volatile long total;

    /**
     * Time to do buffer tasks (in nanoseconds).
     */
    private volatile long spentNanoTime;

    /**
     * Initializes completed, total and spentNanoTime for task.
     *
     * @param completed   copied bytes quantity
     * @param total    total bytes quantity
     * @param spentNanoTime spent time in nanoseconds
     */
    public TaskReport(long completed, long total, long spentNanoTime) {
        this.completed = completed;
        this.total = total;
        this.spentNanoTime = spentNanoTime;
    }

    public long getSpentNanoTime() {
        return spentNanoTime;
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

    public void setSpentNanoTime(long spentNanoTime) {
        this.spentNanoTime = spentNanoTime;
    }

    @Override
    public String toString(){

        return  new StringBuilder().append("TaskReport {" ).append("long completed = ").append(completed)
                .append(", long total = ").append(total).append(", long spentNanoTime = ").append(spentNanoTime)
                .append("; }").toString();
    }
}
