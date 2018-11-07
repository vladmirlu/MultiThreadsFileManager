package com.multithreads.files_manager.management;

import com.multithreads.files_manager.management.command.*;
import com.multithreads.files_manager.management.exception.InvalidCommandException;
import org.apache.log4j.Logger;

import java.util.Scanner;

/**
 * Tool for interaction with user.
 */
public class Communicator {

    /**
     * Root logger.
     */
    private final Logger logger;

    private final FileService service;

    public Communicator(Logger logger){
        this.logger = logger;
        this.service = new FileService(logger);
    }

    /**
     * Interacts with user.
     */
    public void openConsole() {
        Scanner scanner = new Scanner(System.in);

        try {
            switch (getCommand(scanner)) {
                case SPLIT:
                    FileSplitter fileSplitter = new FileSplitter(service);
                    System.out.println("Enter the files quantity you wont to split the file:");
                    fileSplitter.execute(scanner.nextLine());
                case MERGE:
                    FileMerger fileMerger = new FileMerger(service);
                    fileMerger.execute();
                case EXIT:
                    System.out.println("good bye");
                default:
                    openConsole();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        service.shutDownTreads();
    }

    Command getCommand(Scanner scanner) throws InvalidCommandException {
        System.out.println("To split file input " + Command.SPLIT.getSymbol()
                + "\n" + "To merge file input " + Command.MERGE.getSymbol()
                + "\n" + "To exit input " + Command.EXIT.getSymbol());
        Command command = Command.getCommand(scanner.nextLine());
        return command;
    }
}
