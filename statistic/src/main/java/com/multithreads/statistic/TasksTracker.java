package com.multithreads.statistic;

import com.multithreads.statistic.model.TaskReport;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tool for interaction with the src.main.java.statistics module.
 */
public class TasksTracker {
    /**
     * Report about completed and total tasks.
     */
    private final TaskReport commonReport;

    /**
     * Map of section and corresponding task report.
     */
    private final Map<String, TaskReport> allThreadsReports;


    TasksTracker(){
        allThreadsReports = new ConcurrentHashMap<>();
        commonReport = new TaskReport(0,0, 0);
    }

    /**
     * Adds map of section and corresponding task report.
     *
     * @param threadName       name of the section
     */

     void fillAllThreadsReports(long completed, String threadName, long spentTime) {

        if(commonReport.getCompleted() + completed <= commonReport.getTotal()) {

            commonReport.setCompleted(commonReport.getCompleted() + completed);
            commonReport.setSpentTimeNanoSec(commonReport.getSpentTimeNanoSec() + spentTime);

            if (allThreadsReports.containsKey(threadName)) {
                TaskReport threadReport = allThreadsReports.get(threadName);
                threadReport.setCompleted(threadReport.getCompleted() + completed);
                threadReport.setSpentTimeNanoSec(threadReport.getSpentTimeNanoSec() + spentTime);
            } else
                allThreadsReports.put(threadName, new TaskReport(completed, commonReport.getTotal(), spentTime));
        }
    }

    /**
     * Sets total tasks.
     *
     */
     void initAllReports(long total){
        allThreadsReports.clear();
        commonReport.setSpentTimeNanoSec(0);
        commonReport.setCompleted(0);
        commonReport.setTotal(total);
    }


     synchronized TaskReport getCommonReport() {
        return commonReport;
    }

    /**
     * Gets map of section and corresponding task report.
     *
     * @return map of section and corresponding task report
     */
       Map<String, TaskReport> getAllThreadsReports() {
        return allThreadsReports;
    }


}
