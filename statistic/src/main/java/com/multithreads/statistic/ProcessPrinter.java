package com.multithreads.statistic;
import org.apache.log4j.Logger;

/**
 * Prints progress of the tasks.
 */
public class ProcessPrinter implements Runnable {

    /**
     * Statistics logger.
     */
    private final Logger logger;

    /**
     * Tool for interaction with the src.main.java.statistics module.
     */
    private TasksTracker tasksTracker;

    private float totalProgress;
    /**
     * Initializes tasksTracker and commands fields.
     *
     * @param tasksTracker tool for interaction with the src.main.java.statistics module
     */
    public ProcessPrinter(TasksTracker tasksTracker, Logger logger) {
        this.tasksTracker = tasksTracker;
        this.logger = logger;
    }

    /**
     * Executes ProcessPrinter.
     */
    @Override
    public void run() {

       /* try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            throw new RuntimeException(e.getMessage());
        }*/
        logger.trace("Process printer started." + this);

        /*System.out.println("complated = " + tasksTracker.getCommonReport().getCompleted() + "; total = " + tasksTracker.getCommonReport().getTotal());*/

        totalProgress = calculateProgress(tasksTracker.getCommonReport().getCompleted(), tasksTracker.getCommonReport().getTotal());

        System.out.println(buildProgress(totalProgress));
        System.out.flush();

        logger.trace("Process printer finished." + this);
    }

    String buildProgress(float totalProgress) {

        StringBuilder progressBuilder = new StringBuilder();
        progressBuilder.append("Total progress: ").append(totalProgress).append("%, ");
        tasksTracker.getAllThreadsReports().forEach((threadName, taskReport) -> progressBuilder.append(threadName).append(": ")
                .append(calculateProgress(taskReport.getCompleted(), taskReport.getTotal())).append("%, ")
                .append("Spent time: ").append(taskReport.getSpentTimeNanoSec()).append("ns. "));
        progressBuilder.append("Total spent time: ").append(tasksTracker.getCommonReport().getSpentTimeNanoSec()).append("ns ");
        return progressBuilder.toString();
    }

    /**
     * Calculates progress in percentage.
     *
     * @param completed number of completed tasks
     * @param total     number of total tasks
     * @return progress
     */
    public float calculateProgress(long completed, long total) {
        return (float) completed / total * 100;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("ProcessPrinter { ").append("thread name = '")
                .append(Thread.currentThread().getName()).append('\'').append(", common progress = ")
                .append(totalProgress).append('\'').append(", spent time = ")
                .append(tasksTracker.getCommonReport().getSpentTimeNanoSec()).append("ns. ").append('\'')
                .append('}').toString();
    }
}