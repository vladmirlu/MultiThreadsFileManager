package com.multithreads.manager.statistics;

import org.apache.log4j.Logger;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Prints progress of the tasks.
 */
public class ProcessPrinter implements Callable {

    /**
     * Statistics logger.
     */
    private final Logger logger;

    /**
     * Statistics service.
     */
    private final StatisticService statisticService;

    /**
     * Tool for interaction with the src.main.com.multithreads.manager.statistics module.
     */
    private TaskTracker taskTracker;


    /**
     * Initializes taskTracker and constants fields.
     *
     * @param taskTracker tool for interaction with the src.main.com.multithreads.manager.statistics module
     */
    public ProcessPrinter(TaskTracker taskTracker, StatisticService statisticService, Logger logger ) {
        this.taskTracker = taskTracker;
        this.statisticService = statisticService;
        this.logger = logger;
    }

    /**
     * Executes ProcessPrinter.
     */
    @Override
    public String call() {

        String statResult = "";
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
            statResult = buildProgress(completed, all, totalProgress);

            logger.trace("Printed progress." + this);
        }
        logger.trace("ProcessPrinter completed." + this);
        return statResult;
    }

    String buildProgress(Long completed, Long all, Integer totalProgress){

        long timeLeft = statisticService.getCountTimeLeft(taskTracker.getBufferTasks(), taskTracker.getBufferTimeNanoSec(), all - completed);
        Map<String, Integer> taskProgress = statisticService.calculateTasksProgress(taskTracker.getReportsPerSection());
        logger.debug("Completed tasks: " + completed + "." + "Total tasks: " + all + "." + "Total progress: " + totalProgress + "." + "Time remaining: " + timeLeft + "." + "Progress per section: " + taskProgress + this);

        StringBuilder progressBuilder = new StringBuilder();
        progressBuilder.append("Total progress: ").append(totalProgress).append("%, ");
        taskProgress.forEach((threadName, percent) -> progressBuilder.append(threadName).append(": ").append(percent).append("%, "));
        progressBuilder.append("Time remaining: ").append(timeLeft).append("ms");
        return progressBuilder.toString();
    }
}