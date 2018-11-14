package com.multithreads.manager.management;

import com.multithreads.manager.management.constants.*;
import com.multithreads.manager.management.exception.InvalidCommandException;
import com.multithreads.manager.management.workers.FileMerger;
import com.multithreads.manager.management.workers.FileService;
import com.multithreads.manager.management.workers.FileSplitter;
import com.multithreads.manager.statistics.StatisticService;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Tool for interaction with user.
 */
public class Communicator {

    /**
     * Root logger.
     */
    private final Logger logger;

    private final FileSplitter fileSplitter;

    private final FileMerger fileMerger;

    private final FileService fileService;

    private final StatisticService statisticService;

    public Communicator(Logger logger){
        this.logger = logger;
        this.fileService = new FileService(logger);
        this.statisticService = new StatisticService(logger);
        this.fileSplitter = new FileSplitter(fileService, statisticService);
        this.fileMerger = new FileMerger(fileService, statisticService);
    }

    /**
     * Interacts with user.
     */
    public void openConsole() {
        Scanner scanner = new Scanner(System.in);
        try {
            Command command = chooseCommand(scanner);
            switch (command) {
                case SPLIT:
                    System.out.println("Input please the complete file name(with the file path):");
                    String filePath = scanner.nextLine();
                    System.out.println("Input please the size in bytes of file split part:");
                    String splitSize = scanner.nextLine();
                    List<Future<File>> futures = fileSplitter.split(filePath, splitSize);
                    System.out.println("Quantity of split files = " + futures.size());
                    openConsole();
                case MERGE:
                    System.out.println("Input please the directory path to merge all files from there:");
                    System.out.println(fileMerger.merge(scanner.nextLine()).size());
                    openConsole();
                case EXIT:
                    System.out.println("good bye");
                    default:
                        openConsole();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            openConsole();
        } finally {
            fileService.getFileWorkersPool().shutdown();
            statisticService.getStatisticsPool().shutdown();
        }
    }

    Command chooseCommand(Scanner scanner) throws InvalidCommandException {
        System.out.println(Command.SPLIT.getMessage() + Command.SPLIT.getSymbol()
                + "\n" + Command.MERGE.getMessage() + Command.MERGE.getSymbol()
                + "\n" + Command.EXIT.getMessage() + Command.EXIT.getSymbol());
        Command command = Command.getCommand(scanner.nextLine());
        return command;
    }
}
