package com.multithreads.files_manager.management;

import com.multithreads.files_manager.management.command.*;
import com.multithreads.files_manager.management.splitter.*;
import com.multithreads.files_manager.management.splitter.parser.MergeParamParser;
import com.multithreads.files_manager.management.splitter.parser.SplitParamParser;
import com.multithreads.files_manager.management.splitter.provider.PropertiesProvider;
import com.multithreads.files_manager.management.splitter.validator.MergeCommandValidator;
import com.multithreads.files_manager.management.splitter.validator.SplitCommandValidator;
import com.multithreads.files_manager.statistics.TaskTracker;
import org.apache.log4j.Logger;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Tool for interaction with user.
 */
public class Runner {

    /**
     * Root logger.
     */
    private Logger logger;

    /**
     * Error logger.
     */
    private Logger errorLogger;

    /**
     * Tool for providing file properties.
     */
    private PropertiesProvider propertiesProvider = new PropertiesProvider();

    /**
     * File workers thread pool.
     */
    private ExecutorService fileWorkersPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    /**
     * Statistics thread pool.
     */
    private ExecutorService statisticsPool = Executors.newFixedThreadPool(1);

    /**
     * Tool for interaction with the src.main.com.multithreads.files_manager.statistics module.
     */
    private TaskTracker taskTracker = new TaskTrackerImpl();

    /**
     * Initializes loggers.
     *
     * @param logger      root logger
     * @param errorLogger error logger
     */
    public Runner(final Logger logger, final Logger errorLogger) {
        this.logger = logger;
        this.errorLogger = errorLogger;
    }

    /**
     * Interacts with user.
     */
    public void run() {
        FileAssistant fileAssistant = new FileAssistant();
        SplitParamParser splitParamParser = new SplitParamParser(logger);
        MergeParamParser mergeParamParser = new MergeParamParser(logger);
        SplitCommandValidator splitCommandValidator = new SplitCommandValidator(logger);
        MergeCommandValidator mergeCommandValidator = new MergeCommandValidator(logger);
        SplitCommand splitCommand = new SplitCommand(logger, splitParamParser, propertiesProvider, fileWorkersPool,
                                                     statisticsPool, taskTracker, splitCommandValidator);
        MergeCommand mergeCommand = new MergeCommand(logger, fileAssistant, mergeParamParser, propertiesProvider,
                                                     fileWorkersPool, statisticsPool, taskTracker,
                                                     mergeCommandValidator);
        CommandExecutor commandExecutor = new CommandExecutor(logger, splitCommand, mergeCommand);
        Scanner scanner = new Scanner(System.in);
        String clientInput = "";
        while (!clientInput.equals("exit")) {

            System.out.println("Enter the command:");
            clientInput = scanner.nextLine();

            if (!clientInput.equals("exit")) {
                try {
                    commandExecutor.execute(clientInput);
                } catch (Exception ex) {

                    System.out.println("Bad command.");
                }
            }

        }
        fileWorkersPool.shutdown();
        statisticsPool.shutdown();

    }
}
