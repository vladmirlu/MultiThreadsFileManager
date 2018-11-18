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
    private final Logger logger;

    private final FileService fileService;

    public Communicator(Logger logger){
        this.logger = logger;
        this.fileService = new FileService();
    }

    /**
     * Interacts with user.
     */
    public void openConsole() {
        Scanner scanner = new Scanner(System.in);
        try {
            Command command = Command.chooseCommand(scanner);
                    System.out.println("Quantity of tasks = " + command.apply(fileService, scanner).size());
                    openConsole();
        } catch (InvalidCommandException i){
            i.printStackTrace();
            openConsole();
        } catch (IOException ex) {
            ex.printStackTrace();
            openConsole();
        }
        finally {
            fileService.shutdownThreadPools();
            System.out.println("good bye");
        }
    }
}
