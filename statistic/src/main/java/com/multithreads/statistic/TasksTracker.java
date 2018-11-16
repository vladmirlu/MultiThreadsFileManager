package com.multithreads.statistic;

import java.util.HashMap;
import java.util.Map;

/**
 * Tool for interaction with the src.main.java.statistics module.
 */
public class TasksTracker {
    /**
     * Report about completed and total tasks.
     */
    private final TaskReport taskReport;

    /**
     * Map of section and corresponding task report.
     */
    private final Map<String, TaskReport> reportsPerSection ;


    TasksTracker(){
        reportsPerSection = new HashMap<>();
        taskReport = new TaskReport(0,0, 0);
    }

    /**
     * Adds map of section and corresponding task report.
     *
     * @param threadName       name of the section
     */

    public synchronized void addReportPerSection(long completed, String threadName, long total, long time) {

        taskReport.addCompletedTasks(completed);
        taskReport.addTotalSpentTime(time);
        this.reportsPerSection.put(threadName, new TaskReport(completed, total, time));
    }

    /**
     * Sets total tasks.
     *
     */
    public synchronized void setTotalOfTask(long total){
        resetTracker();
        taskReport.setTotal(total);
    }


    public synchronized void resetTracker() {
        taskReport.setTotal(0);
        taskReport.setCompleted(0);
        reportsPerSection.clear();
    }

    public synchronized TaskReport getTaskReport() {
        return taskReport;
    }

    /**
     * Gets map of section and corresponding task report.
     *
     * @return map of section and corresponding task report
     */
    public synchronized Map<String, TaskReport> getReportsPerSection() {
        return reportsPerSection;
    }


}
