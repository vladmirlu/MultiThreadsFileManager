package com.multithreads.statistic;

import org.apache.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Statistics service.
 */
public class StatisticService {

    private final Logger logger;

    public StatisticService(){

        tasksTracker = new TasksTracker();
        logger = Logger.getRootLogger();
    }
    /**
     * Statistics thread pool.
     */
    private final ExecutorService statisticsPool = Executors.newFixedThreadPool(1);

    /**
     * Interface for interaction with the src.main.java.statistics module.
     */
    private TasksTracker tasksTracker;

    public void trackTaskProcess(long completed, String threadName, long time){

             tasksTracker.fillAllThreadsReports(completed, threadName, time);
             statisticsPool.submit(new ProcessPrinter(tasksTracker, logger));
    }

    public ExecutorService getStatisticsPool() {
        return statisticsPool;
    }

    public void initStatistic(long totalSize) {
        tasksTracker.initAllReports(totalSize);
    }
}
