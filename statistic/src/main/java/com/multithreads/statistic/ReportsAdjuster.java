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
    private final TaskReport commonReport;

    /**
     * Entity for logging the process
     */
    private final Logger logger;

    /**
     * ConcurrentHashMap to keep each thread progress.
     */
    private final Map<String, TaskReport> allThreadsReports;

    /**
     * Build new adjuster, init all threads reports, common reports and logger
     */
    ReportsAdjuster(Logger logger) {
        allThreadsReports = new ConcurrentHashMap<>();
        commonReport = new TaskReport(0, 0, 0);
        this.logger = logger;
    }

    /**
     * Adjust reports for each thread and common report.
     *
     * @param copiedBytes   quantity of copied bytes
     * @param threadName    exact thread name
     * @param spentNanoTime spent time in nanoseconds during copy process
     * @return true if common quantity of copied bytes is less then total bytes quantity or false if not
     */
    boolean adjustedThreadReports(long copiedBytes, String threadName, long spentNanoTime) {

        if (commonReport.getCopiedBytes() + copiedBytes >= commonReport.getTotalBytes()) {
            return false;
        }
        commonReport.setCopiedBytes(commonReport.getCopiedBytes() + copiedBytes);
        commonReport.setSpentNanoTime(commonReport.getSpentNanoTime() + spentNanoTime);

        if (allThreadsReports.containsKey(threadName)) {
            TaskReport threadReport = allThreadsReports.get(threadName);
            threadReport.setCopiedBytes(threadReport.getCopiedBytes() + copiedBytes);
            threadReport.setSpentNanoTime(threadReport.getSpentNanoTime() + spentNanoTime);
            logger.debug("Update quantity completed task(" + threadReport.getCopiedBytes() + "bytes) and spent time for thread: '" + threadName + "'");
        } else {
            allThreadsReports.put(threadName, new TaskReport(copiedBytes, commonReport.getTotalBytes(), spentNanoTime));
            logger.debug("Put new TaskReport for thread '" + threadName + "'");
        }
        return true;
    }

    /**
     * Initialize task reports and sets total bytes quantity.
     *
     * @param totalBytes total bytes quantity is to be copied
     */
    void initAllReports(long totalBytes) {
        logger.debug("Set total size of " + totalBytes + "bytes to transfer and reset");
        allThreadsReports.clear();
        commonReport.setSpentNanoTime(0);
        commonReport.setCopiedBytes(0);
        commonReport.setTotalBytes(totalBytes);
    }

    /**
     * Gets common report
     *
     * @return common task report
     */
    synchronized TaskReport getCommonReport() {
        return commonReport;
    }

    /**
     * Gets corresponding tasks reports.
     *
     * @return map of all threads and their reports
     */
    Map<String, TaskReport> getAllThreadsReports() {
        return allThreadsReports;
    }

}
