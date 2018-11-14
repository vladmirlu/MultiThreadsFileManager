package statistics;

import org.apache.log4j.Logger;

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
     * Tool for interaction with the src.main.java.statistics module.
     */
    private TasksTracker tasksTracker;


    /**
     * Initializes tasksTracker and constants fields.
     *
     * @param tasksTracker tool for interaction with the src.main.java.statistics module
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
            totalProgress = statisticService.calculateProgress(tasksTracker.getTaskReport().getCompleted(), tasksTracker.getTaskReport().getTotal());
            System.out.println(buildProgress(totalProgress));

            logger.trace("Printed progress." + this);
        }
        logger.trace("ProcessPrinter completed." + this);

    }

    String buildProgress(Integer totalProgress){

        StringBuilder progressBuilder = new StringBuilder();
        progressBuilder.append("Total progress: ").append(totalProgress).append("%, ");
        tasksTracker.getReportsPerSection().forEach((threadName, taskReport) -> progressBuilder.append(threadName).append(": ").append(statisticService.calculateProgress(taskReport.getCompleted(), taskReport.getTotal())).append("%, ").append("Spent time: ").append(taskReport.getSpentTimeNanoSec()).append("ms. "));
        progressBuilder.append("Total spent time: ").append(tasksTracker.getTaskReport().getSpentTimeNanoSec()).append("ms ");
        return progressBuilder.toString();
    }
}