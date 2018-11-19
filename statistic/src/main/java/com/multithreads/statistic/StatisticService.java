package com.multithreads.statistic;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * Service to make statistic for file management
 */
public class StatisticService {

    /**
     * Root logger
     */
    private final Logger logger;

    /**
     * Build new service to make statistic
     */
    public StatisticService() {
        PropertyConfigurator.configure("statistic/src/main/resources/log4j.properties");
        logger = Logger.getLogger(StatisticService.class);
        reportsAdjuster = new ReportsAdjuster(logger);
    }

    /**
     * Thread pool for writing statistic progress.
     */
    private final ExecutorService statisticsPool = Executors.newFixedThreadPool(1);

    /**
     * Entity adjusting task reports
     */
    private ReportsAdjuster reportsAdjuster;

    /**
     * Track and print tasks progress
     *
     * @param copiedBytes   copied bytes quantity
     * @param threadName    thread name
     * @param spentNanoTime spent time in nanoseconds during copy process
     */
    public void trackTasksProgress(long copiedBytes, String threadName, long spentNanoTime) {

        if (reportsAdjuster.adjustedThreadReports(copiedBytes, threadName, spentNanoTime)) {
            try {
                Future<String> future = statisticsPool.submit(new ProgressBuilder(reportsAdjuster, logger));
                System.out.println(future.get());
                logger.debug("Print the result of thread '" + threadName);

            } catch (InterruptedException e) {
                logger.error("catch InterruptedException " + threadName);
                throw new RuntimeException(e.getMessage());
            } catch (ExecutionException e) {
                logger.error("catch ExecutionException " + threadName);
                e.printStackTrace();
            }
        }
    }

    /**
     * get statistic thread pool
     */
    public ExecutorService getStatisticsPool() {
        return statisticsPool;
    }

    /**
     * Initialize statistic data
     *
     * @param totalBytes total bytes quantity is to be copied
     */
    public void initStatistic(long totalBytes) {
        reportsAdjuster.initAllReports(totalBytes);
        logger.info("Init statistic with total file size of " + totalBytes + "bytes");
    }
}
