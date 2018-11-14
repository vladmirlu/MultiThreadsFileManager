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
        taskReport = new TaskReport(0,0, 0);
        bufferTime = new BufferTime(0,0);

    }

    /**
     * Adds map of section and corresponding task report.
     *
     * @param threadName       name of the section
     */

    public synchronized void addReportPerSection(long toWriteLength, String threadName, long completed, long total, long time) {

        taskReport.addCompletedTasks(toWriteLength);
        taskReport.addTotalTasks(total);
        taskReport.addTotalSpentTime(total);
        this.reportsPerSection.put(threadName, new TaskReport(completed, total, time));
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

    public TaskReport getTaskReport() {
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
