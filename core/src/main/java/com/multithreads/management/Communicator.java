package com.multithreads.management;

import com.multithreads.management.commands.Command;
import com.multithreads.management.exception.InvalidCommandException;
import com.multithreads.management.workers.FileService;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
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
            switch (command) {
                case SPLIT:
                    System.out.println("Input please the complete file name(with the file path):");
                    String filePath = scanner.nextLine();
                    System.out.println("Input please the size in bytes of file split part:");
                    String splitSize = scanner.nextLine();
                    System.out.println("Quantity of split files = " + command.apply(Arrays.asList(filePath, splitSize), fileService).size());
                    openConsole();
                case MERGE:
                    System.out.println("Input please the directory path to merge all files from there:");
                    System.out.println(command.apply(Collections.singletonList(scanner.nextLine()), fileService).size());
                    openConsole();
                default:
                    System.out.println("good bye");
            }
        } catch (InvalidCommandException i){
            i.printStackTrace();
            openConsole();
        } catch (IOException ex) {
            ex.printStackTrace();
            openConsole();
        }
        finally {
            fileService.shutdownThreadPools();
        }
    }
}
