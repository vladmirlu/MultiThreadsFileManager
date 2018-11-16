package com.multithreads.statistic;

import org.apache.log4j.Logger;

/**
 * Prints progress of the tasks.
 */
public class ProcessPrinter extends Thread{

    /**
     * Statistics logger.
     */
    private final Logger logger;

    /**
     * Tool for interaction with the src.main.java.statistics module.
     */
    private TasksTracker tasksTracker;


    /**
     * Initializes tasksTracker and constants fields.
     *
     * @param tasksTracker tool for interaction with the src.main.java.statistics module
     */
    public ProcessPrinter(TasksTracker tasksTracker, Logger logger ) {
        this.tasksTracker = tasksTracker;
        this.logger = logger;
    }

    /**
     * Executes ProcessPrinter.
     */
    @Override
    public void run() {

        logger.trace("ProcessPrinter started." + this);
        int totalProgress = 0;
        while (totalProgress < 100) {

            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                throw new RuntimeException(e.getMessage());
            }

            System.out.println("complated = " + tasksTracker.getTaskReport().getCompleted() + "; total = " + tasksTracker.getTaskReport().getTotal());

            totalProgress = calculateProgress(tasksTracker.getTaskReport().getCompleted(), tasksTracker.getTaskReport().getTotal());

            System.out.println(buildProgress(totalProgress));

            logger.trace("Printed progress." + this);
        }
        logger.trace("ProcessPrinter completed." + this);

    }

    String buildProgress(Integer totalProgress){

        StringBuilder progressBuilder = new StringBuilder();
        progressBuilder.append("Total progress: ").append(totalProgress).append("%, ");
        tasksTracker.getReportsPerSection().forEach((threadName, taskReport) -> progressBuilder.append(threadName).append(": ")
                .append(calculateProgress(taskReport.getCompleted(), taskReport.getTotal())).append("%, ")
                .append("Spent time: ").append(taskReport.getSpentTimeNanoSec()).append("ms. "));
        progressBuilder.append("Total spent time: ").append(tasksTracker.getTaskReport().getSpentTimeNanoSec()).append("ms ");
        return progressBuilder.toString();
    }

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
}