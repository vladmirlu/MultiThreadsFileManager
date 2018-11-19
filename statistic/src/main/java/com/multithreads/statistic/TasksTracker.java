package com.multithreads.statistic;

import com.multithreads.statistic.model.TaskReport;
import org.apache.log4j.Logger;

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

    private final Logger logger;
    /**
     * Map of section and corresponding task report.
     */
    private final Map<String, TaskReport> allThreadsReports;


    TasksTracker(Logger logger) {
        allThreadsReports = new ConcurrentHashMap<>();
        commonReport = new TaskReport(0, 0, 0);
        this.logger = logger;
    }

    /**
     * Adds map of section and corresponding task report.
     *
     * @param threadName name of the section
     */

    void fillAllThreadsReports(long completed, String threadName, long spentTime) {

        if (commonReport.getCompleted() + completed <= commonReport.getTotal()) {

            commonReport.setCompleted(commonReport.getCompleted() + completed);
            commonReport.setSpentTimeNanoSec(commonReport.getSpentTimeNanoSec() + spentTime);

            if (allThreadsReports.containsKey(threadName)) {
                TaskReport threadReport = allThreadsReports.get(threadName);
                threadReport.setCompleted(threadReport.getCompleted() + completed);
                threadReport.setSpentTimeNanoSec(threadReport.getSpentTimeNanoSec() + spentTime);
                logger.debug("Update quantity completed task(" + threadReport.getCompleted() + "bytes) and spent time for thread: '" + threadName + "'");
            } else {
                allThreadsReports.put(threadName, new TaskReport(completed, commonReport.getTotal(), spentTime));
                logger.debug("Put new TaskReport for thread '" + threadName + "'");
            }
        }
    }

    /**
     * Sets total tasks.
     */
    void initAllReports(long total) {
        logger.debug("Set total size of " + total + "bytes to transfer and reset");
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
