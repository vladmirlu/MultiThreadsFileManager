package com.multithreads.statistic;

import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


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
    /**
     * Calculates progress in percentage.
     *
     * @param completed number of completed tasks
     * @param total       number of total tasks
     * @return progress
     */
    public int calculateProgress(long completed, long total) {
        return (int) Math.round((double) completed / ((double) total) * 100);
    }

    public void trackTaskProcess(long completed, String threadName,  long total, long time){

        tasksTracker.addReportPerSection(completed, threadName,  total, time);
         statisticsPool.submit(new ProcessPrinter(tasksTracker, this, logger), tasksTracker.getTaskReport());
    }

    public ExecutorService getStatisticsPool() {
        return statisticsPool;
    }

    public TasksTracker getTasksTracker() {
        return tasksTracker;
    }
}
