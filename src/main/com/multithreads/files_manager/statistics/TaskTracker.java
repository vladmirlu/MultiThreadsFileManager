package com.multithreads.files_manager.statistics;

import java.util.HashMap;
import java.util.Map;

/**
 * Tool for interaction with the src.main.com.multithreads.files_manager.statistics module.
 */
public class TaskTracker {

    /**
     * Report about completed and total tasks.
     */
    private TaskReport taskReport = new TaskReport(0, 0, 0, 0);

    /**
     * Map of section and corresponding task report.
     */
    private Map<String, TaskReport> reportsPerSection = new HashMap<>();


    /**
     * Adds completed tasks.
     *
     * @param completedTasks completed tasks
     */

    public synchronized void addCompletedTasks(final long completedTasks) {
        taskReport.addCompletedTasks(completedTasks);
    }

    /**
     * Adds map of section and corresponding task report.
     *
     * @param name       name of the section
     * @param taskReport report about completed and total tasks
     */

    public synchronized void addReportPerSection(final String name, final TaskReport taskReport) {
        this.reportsPerSection.put(name, taskReport);
    }

    /**
     * Sets total tasks.
     *
     * @param totalTasks total tasks
     */
    public synchronized void setTotalTasks(final long totalTasks) {
        taskReport.setTotal(totalTasks);
    }

    /**
     * Sets completed tasks.
     *
     * @param completedTasks completed tasks
     */
    public synchronized void setCompletedTasks(final long completedTasks) {
        taskReport.setCompleted(completedTasks);
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
     * Sets number of tasks in the buffer.
     *
     * @param buffer buffer of tasks
     */
    public synchronized void setBufferTasks(final long buffer) {
        taskReport.setBuffer(buffer);
    }

    /**
     * Sets time for buffer tasks.
     *
     * @param time time in nanoseconds
     */

    public synchronized void setBufferTimeNanoSec(final long time) {
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
