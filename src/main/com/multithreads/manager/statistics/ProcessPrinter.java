package com.multithreads.manager.statistics;

import org.apache.log4j.Logger;

import java.util.Map;

/**
 * Prints progress of the tasks.
 */
public class ProcessPrinter implements Runnable{

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
    private TasksTracker tasksTracker;


    /**
     * Initializes tasksTracker and constants fields.
     *
     * @param tasksTracker tool for interaction with the src.main.com.multithreads.manager.statistics module
     */
    public ProcessPrinter(TasksTracker tasksTracker, StatisticService statisticService, Logger logger ) {
        this.tasksTracker = tasksTracker;
        this.statisticService = statisticService;
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

            long completed = tasksTracker.getCompletedTasks();
            long all = tasksTracker.getTotalTasks();
            totalProgress = statisticService.calculateProgress(completed, all);

            System.out.println(buildProgress(completed, all, totalProgress));

            logger.trace("Printed progress." + this);
        }
        logger.trace("ProcessPrinter completed." + this);

    }

    String buildProgress(Long completed, Long all, Integer totalProgress){

        long timeLeft = statisticService.getCountTimeLeft(tasksTracker.getBufferTasks(), tasksTracker.getBufferTimeNanoSec(), all - completed);
        Map<String, Integer> taskProgress = statisticService.calculateTasksProgress(tasksTracker.getReportsPerSection());
        logger.debug("Completed tasks: " + completed + "." + "Total tasks: " + all + "." + "Total progress: " + totalProgress + "." + "Time remaining: " + timeLeft + "." + "Progress per section: " + taskProgress + this);

        StringBuilder progressBuilder = new StringBuilder();
        progressBuilder.append("Total progress: ").append(totalProgress).append("%, ");
        taskProgress.forEach((threadName, percent) -> progressBuilder.append(threadName).append(": ").append(percent).append("%, "));
        progressBuilder.append("Time remaining: ").append(timeLeft).append("ms");
        return progressBuilder.toString();
    }
}