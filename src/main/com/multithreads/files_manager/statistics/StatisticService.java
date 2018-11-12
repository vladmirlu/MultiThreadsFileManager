package com.multithreads.files_manager.statistics;

import org.apache.log4j.Logger;

import java.util.HashMap;
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
        this.taskTracker  = new TaskTracker();
    }
    /**
     * Statistics thread pool.
     */
    private final ExecutorService statisticsPool = Executors.newFixedThreadPool(1);

    /**
     * Interface for interaction with the src.main.com.multithreads.files_manager.statistics module.
     */
    private TaskTracker taskTracker;
    /**
     * Calculates progress in percentage.
     *
     * @param completedTasks number of completed tasks
     * @param allTasks       number of total tasks
     * @return progress
     */
    public int calculateProgress(final long completedTasks, final long allTasks) {
        double semiRes = (double) completedTasks / ((double) allTasks) * 100;
        long result = Math.round(semiRes);

        return (int) result;
    }

    /**
     * Calculates progress for each section.
     *
     * @param reports map of section and corresponding task report
     * @return progress for each section
     */
    public Map<String, Integer> calculateTaskProgress(final Map<String, TaskReport> reports) {
        Map<String, Integer> unitProgress = new HashMap<>();
        reports.forEach((id, report) -> unitProgress.put(id, calculateProgress(report.getCompleted(), report.getTotal())));

        return unitProgress;
    }

    /**
     * Calculates time remaining.
     *
     * @param bufferTasks       buffer of bytes
     * @param bufferTimeNanoSec time to read and write buffer bytes (in nanoseconds)
     * @param tasksLeft    remaining tasks
     * @return time remaining
     */
    public long getCountTimeLeft(long bufferTasks, long bufferTimeNanoSec, long tasksLeft) {
        return ((tasksLeft * bufferTimeNanoSec) / bufferTasks) / 1000000;
    }

    public TaskTracker resetTaskTracker(){
        taskTracker.setTotalTasks(0);
        taskTracker.setCompletedTasks(0);
        taskTracker.getReportsPerSection().clear();
        return taskTracker;
    }

    public void trackTaskProcess(long length, String threadName, long alreadyRead, long time){

            taskTracker.addCompletedTasks(length);
            taskTracker.addReportPerSection(threadName, new TaskReport(alreadyRead, length, length, time));
            taskTracker.setBufferTasks(length);
            taskTracker.setBufferTimeNanoSec(time);

    }

    public void getTaskTracking(){
        try {
            Future<TaskTracker> future = statisticsPool.submit(new ProcessPrinter(taskTracker, this), taskTracker);
                future.get();
        } catch (ExecutionException e){
            e.printStackTrace();
        }catch (InterruptedException i){
            i.printStackTrace();
        }
        taskTracker = resetTaskTracker();
    }

    public ExecutorService getStatisticsPool() {
        return statisticsPool;
    }
}
