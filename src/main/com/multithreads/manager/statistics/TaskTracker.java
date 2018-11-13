package com.multithreads.manager.statistics;

import java.util.HashMap;
import java.util.Map;

/**
 * Tool for interaction with the src.main.com.multithreads.manager.statistics module.
 */
public class TaskTracker {

    /**
     * Report about completed and total tasks.
     */
    private final TaskReport taskReport;

    /**
     * Map of section and corresponding task report.
     */
    private final Map<String, TaskReport> reportsPerSection ;


    TaskTracker(){
        reportsPerSection = new HashMap<>();
        taskReport = new TaskReport(0, 0, 0, 0);
    }

    /**
     * Adds completed tasks.
     *
     * @param completedTasks completed tasks
     */

    public synchronized void addCompletedTasks(long completedTasks) {
        taskReport.addCompletedTasks(completedTasks);
    }

    /**
     * Adds map of section and corresponding task report.
     *
     * @param name       name of the section
     * @param taskReport report about completed and total tasks
     */

    public synchronized void addReportPerSection(String name, TaskReport taskReport) {
        this.reportsPerSection.put(name, taskReport);
    }

    /**
     * Sets total tasks.
     *
     * @param totalTasks total tasks
     */
    public synchronized void setTotalTasks(long totalTasks) {
        taskReport.setTotalTasks(totalTasks);
    }

    /**
     * Sets completed tasks.
     *
     * @param completedTasks completed tasks
     */
    public synchronized void setCompletedTasks(long completedTasks) {
        taskReport.setCompletedTasks(completedTasks);
    }

    /**
     * Gets completed tasks.
     *
     * @return completed tasks
     */
    public synchronized long getCompletedTasks() {
        return taskReport.getCompletedTasks();
    }

    /**
     * Gets total tasks.
     *
     * @return total tasks
     */

    public synchronized long getTotalTasks() {
        return taskReport.getTotalTasks();
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
     * Sets number of tasks in the buffer.
     *
     * @param buffer buffer of tasks
     */
    public synchronized void setTasksBuffer(long buffer) {
        taskReport.setBuffer(buffer);
    }

    /**
     * Sets time for buffer tasks.
     *
     * @param time time in nanoseconds
     */

    public synchronized void setBufferTimeNanoSec(long time) {
        taskReport.setTimeNanoSec(time);
    }

    /**
     * Gets number of tasks in the buffer.
     *
     * @return number of tasks in the buffer
     */

    public synchronized long getBufferTasks() {
        return taskReport.getBuffer();
    }

    /**
     * Gets time for buffer tasks.
     *
     * @return time in nanoseconds
     */

    public synchronized long getBufferTimeNanoSec() {
        return taskReport.getTimeNanoSec();
    }

}
