package com.multithreads.files_manager.management;

import com.multithreads.files_manager.management.command.*;
import com.multithreads.files_manager.management.exception.InvalidCommandException;
import com.multithreads.files_manager.management.splitter.*;
import com.multithreads.files_manager.management.splitter.parser.MergeParamParser;
import com.multithreads.files_manager.management.splitter.parser.SplitParamParser;
import com.multithreads.files_manager.management.splitter.provider.PropertiesProvider;
import com.multithreads.files_manager.statistics.TaskTracker;
import org.apache.log4j.Logger;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Tool for interaction with user.
 */
public class Communicator {

    /**
     * Root logger.
     */
    private Logger logger;

    /**
     * Tool for providing file properties.
     */
    private PropertiesProvider propertiesProvider = new PropertiesProvider();

    /**
     * Tool for interaction with the src.main.com.multithreads.files_manager.statistics module.
     */
    private TaskTracker taskTracker = new TaskTrackerImpl();

    /**
     * Initializes loggers.
     *
     * @param logger root logger
     */
    public Communicator(final Logger logger) {
        this.logger = logger;
    }

    /**
     * Interacts with user.
     */
    public void openConsole() {
        Scanner scanner = new Scanner(System.in);
        FileAssistant fileAssistant = new FileAssistant();

        try {
            switch (getCommand(scanner)) {
                case SPLIT:
                    FileSplitter fileSplitter = new FileSplitter(logger, propertiesProvider, taskTracker);
                    System.out.println("Enter the files quantity you wont to split the file:");
                    fileSplitter.execute(scanner.nextLine());
                case MERGE:
                    FileMerger fileMerger = new FileMerger(logger, fileAssistant, propertiesProvider, taskTracker);
                    fileMerger
                case EXIT:
                    System.out.println("good bye");
                default:
                    openConsole();
            }

        } catch (Exception ex) {
            ex.printStackTrace();

        }
        fileWorkersPool.shutdown();
        statisticsPool.shutdown();
    }

    Command getCommand(Scanner scanner) throws InvalidCommandException {
        System.out.println("To split file input " + Command.SPLIT.getSymbol()
                + "\n" + "To merge file input " + Command.MERGE.getSymbol()
                + "\n" + "To exit input " + Command.EXIT.getSymbol());
        Command command = Command.getCommand(scanner.nextLine());
        return command;
    }
}
