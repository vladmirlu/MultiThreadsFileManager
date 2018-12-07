package com.multithreads.statistic;

import com.multithreads.statistic.model.TaskReport;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Entity to adjust statistic task reports
 */
public class ReportsAdjuster {
    /**
     * Report about completed and total tasks.
     */
    private final TaskReport generalReport;

    /**
     * Entity for logging the process
     */
    private final Logger logger = Logger.getLogger(ReportsAdjuster.class);

    /**
     * ConcurrentHashMap to keep each thread progress.
     */
    private final Map<String, TaskReport> threadReports;

    /**
     * Build new adjuster, init all threads reports, common reports and logger
     */
    ReportsAdjuster() {
        threadReports = new ConcurrentHashMap<>();
        generalReport = new TaskReport(0, 0, 0);
    }

    /**
     * Adjust reports for each thread and common report.
     *
     * @param copiedBytes   quantity of copied bytes
     * @param threadName    exact thread name
     * @param spentNanoTime spent time in nanoseconds during copy process
     * @return true if general quantity of copied bytes is less then the total bytes quantity or false if not
     */
    boolean adjustThreadReports(long copiedBytes, String threadName, long spentNanoTime) {

        if (generalReport.getCompleted() + copiedBytes >= generalReport.getTotal()) {
            return false;
        }
        generalReport.setCompleted(generalReport.getCompleted() + copiedBytes);
        generalReport.setSpentNanoTime(generalReport.getSpentNanoTime() + spentNanoTime);
        logger.debug("Update data of general task report: " + generalReport.toString());

        TaskReport threadReport;
        if (threadReports.containsKey(threadName)) {
            threadReport = threadReports.get(threadName);
            threadReport.setCompleted(threadReport.getCompleted() + copiedBytes);
            threadReport.setSpentNanoTime(threadReport.getSpentNanoTime() + spentNanoTime);
            logger.debug("Update data of one thread report" + threadReport.toString() + " for thread: '" + threadName + "'");
        } else {
            threadReport = new TaskReport(copiedBytes, generalReport.getTotal(), spentNanoTime);
            threadReports.put(threadName, threadReport);
            logger.debug("Put new TaskReport: " + threadReport.toString() + " for thread '" + threadName + "'");
        }
        return true;
    }

    /**
     * Initialize task reports and sets total bytes quantity.
     *
     * @param totalBytes total bytes quantity is to be copied
     */
    void initAllReports(long totalBytes) {
        logger.debug("Set total size of " + totalBytes + "bytes to transfer and reset other fields");
        threadReports.clear();
        generalReport.setSpentNanoTime(0);
        generalReport.setCompleted(0);
        generalReport.setTotal(totalBytes);
    }

    /**
     * Gets general report
     *
     * @return general report of all tasks
     */
    synchronized TaskReport getGeneralReport() {
        return generalReport;
    }

    /**
     * Gets corresponding task reports.
     *
     * @return map of all threads and their reports
     */
    Map<String, TaskReport> getThreadReports() {
        return threadReports;
    }

}
