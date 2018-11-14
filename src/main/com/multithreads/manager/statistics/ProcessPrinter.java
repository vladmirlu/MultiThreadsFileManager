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

            long completed = tasksTracker.getTaskReport().getCompleted();
            System.out.println("completed" + completed);
            long total = tasksTracker.getTaskReport().getTotal();
            System.out.println("total" + total);

            totalProgress = statisticService.calculateProgress(completed, total);
            System.out.println(buildProgress(completed, total, totalProgress));

            logger.trace("Printed progress." + this);
        }
        logger.trace("ProcessPrinter completed." + this);

    }

    String buildProgress(Long completed, Long total, Integer totalProgress){

        /*long timeLeft = statisticService.getCountTimeLeft(tasksTracker.getBufferTasks(), tasksTracker.getBufferTimeNanoSec(), total - completed);*/
        /*Map<String, Integer> taskProgress = statisticService.calculateTasksProgress(tasksTracker.getReportsPerSection());*/
        /*logger.debug("Completed tasks: " + completed + "." + "Total tasks: " + total + "." + "Total progress: " + totalProgress + "." + "Time remaining: " + timeLeft + "." + "Progress per section: " + taskProgress + this);*/

        StringBuilder progressBuilder = new StringBuilder();
        progressBuilder.append("Total progress: ").append(totalProgress).append("%, ");
        tasksTracker.getReportsPerSection().forEach((threadName, taskReport) -> progressBuilder.append(threadName).append(": ").append(statisticService.calculateProgress(taskReport.getCompleted(), taskReport.getTotal())).append("%, ").append("Spent time: ").append(taskReport.getTimeNanoSec()).append("ms. "));
        progressBuilder.append("Total spent time: ").append(tasksTracker.getTaskReport().getTimeNanoSec()).append("ms ");
        return progressBuilder.toString();
    }
}