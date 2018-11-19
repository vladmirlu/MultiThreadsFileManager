package com.multithreads.statistic;

import org.apache.log4j.Logger;

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

        logger = Logger.getLogger(StatisticService.class);
        tasksTracker = new TasksTracker(logger);
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
             try {
                 Future<String> future = statisticsPool.submit(new ProgressBuilder(tasksTracker, logger));
                     System.out.println(future.get());
                     logger.debug("Print the result of thread '" + threadName );

             } catch (InterruptedException e) {
                 logger.error("catch InterruptedException " + threadName );
                 throw new RuntimeException(e.getMessage());
             }catch (ExecutionException e){
                 logger.error("catch ExecutionException " + threadName );
                 e.printStackTrace();
             }
    }

    public ExecutorService getStatisticsPool() {
        return statisticsPool;
    }

    public void initStatistic(long totalSize) {
        tasksTracker.initAllReports(totalSize);
        logger.info("Init statistic with total file size of " + totalSize + "bytes");
    }

}
