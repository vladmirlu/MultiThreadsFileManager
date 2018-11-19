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
     * Tool for interaction with the src.main.java.statistics module.
     */
    private TasksTracker tasksTracker;

    private int totalProgress;
    /**
     * Initializes tasksTracker and commands fields.
     *
     * @param tasksTracker tool for interaction with the src.main.java.statistics module
     */
    public ProgressBuilder(TasksTracker tasksTracker, Logger logger) {
        this.tasksTracker = tasksTracker;
        this.logger = logger;
    }

    /**
     * Executes ProgressBuilder.
     */
    @Override
    public String call(){
        try{
            Thread.sleep(1);
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        logger.trace("Process building started." + this);
        totalProgress = calculateProgress(tasksTracker.getCommonReport().getCompleted(), tasksTracker.getCommonReport().getTotal());

            StringBuilder progressBuilder = new StringBuilder();
            progressBuilder.append("Total progress: ").append(totalProgress).append("%, ");
            tasksTracker.getAllThreadsReports().forEach((threadName, taskReport) -> progressBuilder.append(threadName).append(": ")
                    .append(calculateProgress(taskReport.getCompleted(), taskReport.getTotal())).append("%, ")
                    .append("Spent time: ").append(taskReport.getSpentTimeNanoSec()).append("ns. "));
            progressBuilder.append("Total spent time: ").append(tasksTracker.getCommonReport().getSpentTimeNanoSec()).append("ns ");
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

    @Override
    public String toString() {
        return new StringBuilder().append("ProgressBuilder { ").append("thread name = '")
                .append(Thread.currentThread().getName()).append('\'').append(", common progress = ")
                .append(totalProgress).append('\'').append(", spent time = ")
                .append(tasksTracker.getCommonReport().getSpentTimeNanoSec()).append("ns. ").append('\'')
                .append('}').toString();
    }
}