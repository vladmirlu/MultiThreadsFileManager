package com.multithreads.manager.statistics;

/**
 * Report about completedTasks and totalTasks tasks.
 */
public class TaskReport {

    /**
     * Number of completedTasks tasks.
     */
    private long completedTasks;

    /**
     * Number of totalTasks tasks.
     */
    private long totalTasks;

    /**
     * Buffer of tasks.
     */
    private long buffer;

    /**
     * Time to do buffer tasks (in nanoseconds).
     */
    private long timeNanoSec;

    /**
     * Initializes completedTasks and totalTasks tasks fields.
     *
     * @param completedTasks completedTasks tasks
     * @param totalTasks totalTasks tasks
     */
    public TaskReport(long completedTasks, long totalTasks, long buffer, long timeNanoSec) {
        this.completedTasks = completedTasks;
        this.totalTasks = totalTasks;
        this.buffer = buffer;
        this.timeNanoSec = timeNanoSec;
    }

    public long getCompletedTasks() {
        return completedTasks;
    }

    public long getTotalTasks() {
        return totalTasks;
    }

    public long getBuffer() {
        return buffer;
    }

    public void setBuffer(long buffer) {
        this.buffer = buffer;
    }

    public long getTimeNanoSec() {
        return timeNanoSec;
    }

    public void setTimeNanoSec(long timeNanoSec) {
        this.timeNanoSec = timeNanoSec;
    }

    public void setCompletedTasks(long completedTasks) {
        this.completedTasks = completedTasks;
    }

    public void setTotalTasks(long totalTasks) {
        this.totalTasks = totalTasks;
    }

    public void addCompletedTasks(long completed) {
        this.completedTasks = this.completedTasks + completed;
    }

    @Override
    public String toString() {
        return "TaskReport{" +
                "completedTasks=" + completedTasks +
                ", totalTasks=" + totalTasks +
                '}';
    }
}
