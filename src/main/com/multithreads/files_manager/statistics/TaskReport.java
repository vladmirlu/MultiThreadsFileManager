package com.multithreads.files_manager.statistics;

/**
 * Report about completed and total tasks.
 */
public class TaskReport {

    /**
     * Number of completed tasks.
     */
    private long completed;

    /**
     * Number of total tasks.
     */
    private long total;

    /**
     * Buffer of tasks.
     */
    private long buffer;

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
    public TaskReport(long completed, long total, long buffer, long timeNanoSec) {
        this.completed = completed;
        this.total = total;
        this.buffer = buffer;
        this.timeNanoSec = timeNanoSec;
    }

    public long getCompleted() {
        return completed;
    }

    public long getTotal() {
        return total;
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

    public void setCompleted(long completed) {
        this.completed = completed;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public void addCompletedTasks(long completed) {
        this.completed = this.completed + completed;
    }

    @Override
    public String toString() {
        return "TaskReport{" +
                "completed=" + completed +
                ", total=" + total +
                '}';
    }
}
