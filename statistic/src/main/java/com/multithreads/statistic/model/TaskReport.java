package com.multithreads.statistic.model;

/**
 * Report about copiedBytes and totalBytes tasks and spent time.
 */
public class TaskReport {

    /**
     * Quantity of copied bytes
     */
    private volatile long copiedBytes;

    /**
     * Total bytes quantity
     */
    private volatile long totalBytes;

    /**
     * Time to do buffer tasks (in nanoseconds).
     */
    private volatile long spentNanoTime;

    /**
     * Initializes copiedBytes, totalBytes and spentNanoTime for task.
     *
     * @param copiedBytes   copied bytes quantity
     * @param totalBytes    total bytes quantity
     * @param spentNanoTime spent time in nanoseconds
     */
    public TaskReport(long copiedBytes, long totalBytes, long spentNanoTime) {
        this.copiedBytes = copiedBytes;
        this.totalBytes = totalBytes;
        this.spentNanoTime = spentNanoTime;
    }

    public long getSpentNanoTime() {
        return spentNanoTime;
    }

    public long getCopiedBytes() {
        return copiedBytes;
    }

    public long getTotalBytes() {
        return totalBytes;
    }

    public void setCopiedBytes(long copiedBytes) {
        this.copiedBytes = copiedBytes;
    }

    public void setTotalBytes(long totalBytes) {
        this.totalBytes = totalBytes;
    }

    public void setSpentNanoTime(long spentNanoTime) {
        this.spentNanoTime = spentNanoTime;
    }
}
