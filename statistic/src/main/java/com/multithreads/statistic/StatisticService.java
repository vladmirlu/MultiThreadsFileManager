package com.multithreads.statistic;

import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * Service to make statistic
 */
public class StatisticService {

    /**
     * Root logger
     */
    private final Logger logger = Logger.getLogger(StatisticService.class);

    /**
     * Thread pool for writing statistic progress.
     */
    private final ExecutorService statisticsPool = Executors.newFixedThreadPool(1);

    /**
     * Entity adjusting task reports
     */
    private ReportsAdjuster reportsAdjuster;

    /**
     * Time to set interval of statistic print
     */
    private int sleepTimeMillis;

    /**
     * Statistic future result
     */
    private Future<String> result;

    /**
     * flag to turn on or shut down statistic printing
     */
    private volatile boolean printOn = false;

    /**
     * Build new service to make statistic and initialize @sleepTimeMillis from property file
     */
    public StatisticService(String resourcesPath) {
        reportsAdjuster = new ReportsAdjuster();
        try {
            FileInputStream fis = new FileInputStream(resourcesPath);
            sleepTimeMillis = Integer.parseInt(new PropertyResourceBundle(fis).getString("sleepTimeMillis"));
        } catch (MissingResourceException m) {
            m.printStackTrace();
            logger.error("Resource is missing: " + resourcesPath + "MissingResourceException occur: " + m.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("IOException is occur during reading resources from the file '" + resourcesPath + "'; " + e.getMessage());
        }
    }

    /**
     * Track task progress if there are actual reports or set print flag to false
     *
     * @param copiedBytes   copied bytes quantity
     * @param threadName    thread name
     * @param spentNanoTime spent time in nanoseconds during copy process
     */
    public synchronized void trackTaskProgress(long copiedBytes, String threadName, long spentNanoTime) {

        if (reportsAdjuster.adjustThreadReports(copiedBytes, threadName, spentNanoTime)) {
            result = statisticsPool.submit(new ProgressBuilder(reportsAdjuster));
            logger.debug("Print the result when copiedBytes= " + copiedBytes + "; spent time=" + spentNanoTime + "nanosec.");
        } else printOn = false;
    }

    /**
     * Print statistic tasks progress
     */
    public  void printStatistic() {
        printOn = true;
        try {
            while (printOn) {
                Thread.sleep(sleepTimeMillis);
                    System.out.println(result.get());
            }
        } catch (InterruptedException e) {
            logger.error("InterruptedException occurs: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        } catch (ExecutionException e) {
            logger.error("ExecutionException occurs: " + e.getMessage());
            e.printStackTrace();
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
