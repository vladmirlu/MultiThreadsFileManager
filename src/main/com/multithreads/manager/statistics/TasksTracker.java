package com.multithreads.manager.statistics;

import java.util.HashMap;
import java.util.Map;

/**
 * Tool for interaction with the src.main.com.multithreads.manager.statistics module.
 */
public class TasksTracker {
    /**
     * Report about completed and total tasks.
     */
    private TaskReport taskReport;

    private BufferTime bufferTime;

    /**
     * Map of section and corresponding task report.
     */
    private final Map<String, TaskReport> reportsPerSection ;


    TasksTracker(){
        reportsPerSection = new HashMap<>();
        taskReport = new TaskReport(0,0);
        bufferTime = new BufferTime(0,0);

    }

    /**
     * Adds map of section and corresponding task report.
     *
     * @param threadName       name of the section
     */

    public synchronized void addReportPerSection(long fileToWriteLength, String threadName, long alreadyRead, long time) {

        taskReport.addCompletedTasks(fileToWriteLength);
        bufferTime.setBuffer(fileToWriteLength);
        bufferTime.setTimeNanoSec(time);
        this.reportsPerSection.put(threadName, new TaskReport(fileToWriteLength, alreadyRead));
    }

    /**
     * Sets total tasks.
     *
     */
    public synchronized void resetTracker() {
        taskReport.setTotal(0);
        taskReport.setCompleted(0);
        reportsPerSection.clear();
    }

    /**
     * Gets completed tasks.
     *
     * @return completed tasks
     */
    public synchronized long getCompletedTasks() {
        return taskReport.getCompleted();
    }

    /**
     * Gets total tasks.
     *
     * @return total tasks
     */

    public synchronized long getTotalTasks() {
        return taskReport.getTotal();
    }

    /**
     * Gets map of section and corresponding task report.
     *
     * @return map of section and corresponding task report
     */
    public synchronized Map<String, TaskReport> getReportsPerSection() {
        return reportsPerSection;
    }

    /**
     * Gets number of tasks in the buffer.
     *
     * @return number of tasks in the buffer
     */

    public synchronized long getBufferTasks() {
        return bufferTime.getBuffer();
    }

    /**
     * Gets time for buffer tasks.
     *
     * @return time in nanoseconds
     */

    public synchronized long getBufferTimeNanoSec() {
        return bufferTime.getTimeNanoSec();
    }

}
