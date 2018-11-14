package com.multithreads.manager.statistics;

import org.apache.log4j.Logger;

import java.util.HashMap;
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

    public StatisticService(Logger logger){
        this.logger = logger;
        this.tasksTracker = new TasksTracker();
    }
    /**
     * Statistics thread pool.
     */
    private final ExecutorService statisticsPool = Executors.newFixedThreadPool(1);

    /**
     * Interface for interaction with the src.main.com.multithreads.manager.statistics module.
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
        Future<Map<String, TaskReport>> future = statisticsPool.submit(new ProcessPrinter(tasksTracker, this, logger), tasksTracker.getReportsPerSection());
       // System.out.println(getTaskTracking(future));
    }

    public String getTaskTracking(Future<Map<String, TaskReport>> future){
        StringBuilder builder = new StringBuilder();
        try {
            Iterator entries = future.get().entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry entry = (Map.Entry) entries.next();
                builder.append("\nKey = ").append(entry.getKey()).append(", Value = ").append(entry.getValue());
            }
        } catch (ExecutionException e){
            e.printStackTrace();
        }catch (InterruptedException i){
            i.printStackTrace();
        }
        //tasksTracker = resetTaskTracker();
        return builder.toString();
    }

    public ExecutorService getStatisticsPool() {
        return statisticsPool;
    }

    public TasksTracker getTasksTracker() {
        return tasksTracker;
    }
}
