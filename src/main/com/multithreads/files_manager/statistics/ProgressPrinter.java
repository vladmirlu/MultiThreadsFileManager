package com.multithreads.files_manager.statistics;

import org.apache.log4j.Logger;

import java.util.Map;

/**
 * Prints progress of the tasks.
 */
public class ProgressPrinter extends Thread {

    /**
     * Statistics logger.
     */
    private static final Logger logger = Logger.getLogger("stat-logs");

    /**
     * Statistics service.
     */
    private StatisticsService statisticsService = new StatisticsServiceImpl();

    /**
     * Tool for interaction with the src.main.com.multithreads.files_manager.statistics module.
     */
    private TaskTracker taskTracker;

    /**
     * User command.
     */
    private String command;

    /**
     * Initializes taskTracker and command fields.
     *
     * @param taskTracker tool for interaction with the src.main.com.multithreads.files_manager.statistics module
     * @param command     user command
     */
    public ProgressPrinter(TaskTracker taskTracker, String command) {
        this.taskTracker = taskTracker;
        this.command = command;
    }

    /**
     * Executes ProgressPrinter.
     */
    @Override
    public void run() {
        logger.trace("ProgressPrinter started." + this);
        int totalProgress = 0;
        while (totalProgress < 100) {
            try {
                sleep(400);
            } catch (InterruptedException e) {
                throw new RuntimeException(e.getMessage());
            }
            long completed = taskTracker.getCompletedTasks();
            long all = taskTracker.getTotalTasks();
            totalProgress = statisticsService.calculateProgress(completed, all);
            long timeRemaining = statisticsService.calculateTimeRemaining(taskTracker.getBufferTasks(), taskTracker
                    .getBufferTimeNanoSec(), all - completed);
            Map<String, Integer> progressPerSection = statisticsService
                    .calculateProgressPerSection(taskTracker.getReportsPerSection());
            logger.trace("Completed tasks: " + completed + "." + "Total tasks: " + all + "."
                                 + "Total progress: " + totalProgress + "." + "Time remaining: " + timeRemaining + "."
                                 + "Progress per section: " + progressPerSection + this);
            StringBuilder progress = new StringBuilder();
            progress.append("Total progress: ").append(totalProgress).append("%, ");
            progressPerSection.forEach((name, percent) ->
                                               progress.append(name).append(": ").append(percent).append("%, "));
            progress.append("Time remaining: ").append(timeRemaining).append("ms");
            System.out.println(progress);
            logger.trace("Printed progress." + this);
        }
        logger.trace("ProgressPrinter completed." + this);
    }

    @Override
    public String toString() {
        return "ProgressPrinter{" +
                "threadName='" + Thread.currentThread().getName() + '\'' +
                ", command='" + command + '\'' +
                '}';
    }
}