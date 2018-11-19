package com.multithreads.statistic;

import org.apache.log4j.Logger;

import java.util.concurrent.Callable;

/**
 * Prints progress of the tasks.
 */
public class ProgressBuilder implements Callable {

    /**
     * Statistics logger.
     */
    private final Logger logger;

    /**
     * Entity to adjust reports during statistic process
     */
    private ReportsAdjuster reportsAdjuster;

    /**
     * total progress in percentage
     */
    private int totalProgress;

    /**
     * Builds new process builder, initializes reports adjuster and logger.
     *
     * @param reportsAdjuster adjust reports during statistic process
     * @param logger          logs the statistic process
     */
    public ProgressBuilder(ReportsAdjuster reportsAdjuster, Logger logger) {
        this.reportsAdjuster = reportsAdjuster;
        this.logger = logger;
    }

    /**
     * Executes progress builder.
     */
    @Override
    public String call() {

        logger.trace("Process building started." + this);
        totalProgress = calculateProgress(reportsAdjuster.getCommonReport().getCopiedBytes(), reportsAdjuster.getCommonReport().getTotalBytes());

        StringBuilder progressBuilder = new StringBuilder();
        progressBuilder.append("Total progress: ").append(totalProgress).append("%, ");
        reportsAdjuster.getAllThreadsReports().forEach((threadName, taskReport) -> progressBuilder.append(threadName).append(": ")
                .append(calculateProgress(taskReport.getCopiedBytes(), taskReport.getTotalBytes())).append("%, ")
                .append("Spent time: ").append(taskReport.getSpentNanoTime()).append("ns. "));
        progressBuilder.append("Total spent time: ").append(reportsAdjuster.getCommonReport().getSpentNanoTime()).append("ns ");
        logger.trace("Process printer finished." + this);
        return progressBuilder.toString();
    }

    /**
     * Calculates progress in percentage.
     *
     * @param completed number of completed tasks
     * @param total     number of total tasks
     * @return progress
     */
    public int calculateProgress(long completed, long total) {
        return Math.round((float) completed / total * 100);
    }

    /**
     * Build override toString() method to print current task in readable format
     */
    @Override
    public String toString() {
        return new StringBuilder().append("ProgressBuilder { ").append("thread name = '")
                .append(Thread.currentThread().getName()).append('\'').append(", common progress = ")
                .append(totalProgress).append('\'').append(", spent time = ")
                .append(reportsAdjuster.getCommonReport().getSpentNanoTime()).append("ns. ").append('\'')
                .append('}').toString();
    }
}