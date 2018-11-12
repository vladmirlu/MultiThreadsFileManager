package com.multithreads.files_manager.statistics;

import org.apache.log4j.Logger;

import java.util.Map;

/**
 * Prints progress of the tasks.
 */
public class ProcessPrinter implements Runnable {

    /**
     * Statistics logger.
     */
    private static final Logger logger = Logger.getLogger("stat-logs");

    /**
     * Statistics service.
     */
    private final StatisticService statisticService;

    /**
     * Tool for interaction with the src.main.com.multithreads.files_manager.statistics module.
     */
    private TaskTracker taskTracker;


    /**
     * Initializes taskTracker and constants fields.
     *
     * @param taskTracker tool for interaction with the src.main.com.multithreads.files_manager.statistics module
     */
    public ProcessPrinter(TaskTracker taskTracker, StatisticService statisticService) {
        this.taskTracker = taskTracker;
        this.statisticService = statisticService;
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

            long completed = taskTracker.getCompletedTasks();
            long all = taskTracker.getTotalTasks();

            totalProgress = statisticService.calculateProgress(completed, all);
            System.out.println(buildProgress(completed, all, totalProgress));

            logger.trace("Printed progress." + this);
        }
        logger.trace("ProcessPrinter completed." + this);
    }

    String buildProgress(Long completed, Long all, Integer totalProgress){

        long timeRemaining = statisticService.getCountTimeLeft(taskTracker.getBufferTasks(), taskTracker.getBufferTimeNanoSec(), all - completed);
        Map<String, Integer> taskProgress = statisticService.calculateTaskProgress(taskTracker.getReportsPerSection());
        logger.trace("Completed tasks: " + completed + "." + "Total tasks: " + all + "." + "Total progress: " + totalProgress + "." + "Time remaining: " + timeRemaining + "." + "Progress per section: " + taskProgress + this);

        StringBuilder progressBuilder = new StringBuilder();
        progressBuilder.append("Total progress: ").append(totalProgress).append("%, ");
        taskProgress.forEach((name, percent) -> progressBuilder.append(name).append(": ").append(percent).append("%, "));
        progressBuilder.append("Time remaining: ").append(timeRemaining).append("ms");
        return progressBuilder.toString();
    }
}