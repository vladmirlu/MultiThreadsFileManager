package com.multithreads.management;

import com.multithreads.management.commands.Command;
import com.multithreads.management.exception.InvalidCommandException;
import com.multithreads.management.workers.FileService;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Scanner;


/**
 * Tool for interaction with user.
 */
public class Communicator {

    /**
     * Root logger.
     */
    private final Logger logger = Logger.getLogger(Communicator.class);

    /**
     * Service to work with file operations
     */
    private final FileService fileService;

    /**
     * Build new communicator to interact with with user via console
     */
    public Communicator() {
        String resourcesPath = "core/src/main/resources/application.properties";
        this.fileService = new FileService(resourcesPath);
    }

    /**
     * Interacts with user, receives data from console and transmit it into program
     */
    public void openConsole() {
        Scanner scanner = new Scanner(System.in);
        try {
            Command command = Command.chooseCommand(scanner);
            System.out.println("Quantity of tasks = " + command.apply(fileService, scanner).size());
            logger.info("Quantity of Future<File> tasks = " + command.apply(fileService, scanner).size());
            openConsole();
        } catch (InvalidCommandException i) {
            i.printStackTrace();
            logger.error("Invalid command. " + i.getMessage());
            openConsole();
        } catch (IOException ex) {
            ex.printStackTrace();
            logger.error("IOException: " + ex.getMessage());
            openConsole();
        } finally {
            fileService.shutdownThreadPools();
            logger.info("Program finished " + this);
            System.out.println("Good bye");
        }
    }

    /**
     * Build override toString() method to print Communicator object readable format
     */
    @Override
    public String toString() {
        return new StringBuilder().append("ConsoleCommunicator { ").append(this.getClass()).append(", hashCode= ")
                .append(this.hashCode()).append("; ").append(this.fileService.toString()).append(" }").toString();
    }
}
