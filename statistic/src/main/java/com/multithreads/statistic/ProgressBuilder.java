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
    private final Logger logger = Logger.getLogger(ProgressBuilder.class);

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
     */
    public ProgressBuilder(ReportsAdjuster reportsAdjuster) {
        this.reportsAdjuster = reportsAdjuster;
    }

    /**
     * Executes progress builder.
     */
    @Override
    public String call() {

        logger.info("Start statistic reports building: " + this);
        totalProgress = calculateProgress(reportsAdjuster.getGeneralReport().getCopiedBytes(), reportsAdjuster.getGeneralReport().getTotalBytes());

        StringBuilder progressBuilder = new StringBuilder();
        progressBuilder.append("Total progress: ").append(totalProgress).append("%, ");
        reportsAdjuster.getThreadReports().forEach((threadName, taskReport) -> progressBuilder.append(threadName).append(": ")
                .append(calculateProgress(taskReport.getCopiedBytes(), taskReport.getTotalBytes())).append("%, ")
                .append("Spent time: ").append(taskReport.getSpentNanoTime()).append("ns. "));
        progressBuilder.append("Total spent time: ").append(reportsAdjuster.getGeneralReport().getSpentNanoTime()).append("ns ");
        logger.info("End statistic reports building:" + this);
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
        return new StringBuilder().append("ProgressBuilder { ").append(",general progress= ")
                .append(totalProgress).append('\'').append(", spent time= ")
                .append(reportsAdjuster.getGeneralReport().getSpentNanoTime()).append("ns. ").append('\'')
                .append('}').toString();
    }
}