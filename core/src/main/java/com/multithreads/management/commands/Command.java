package com.multithreads.management.commands;

import com.multithreads.management.exception.InvalidCommandException;
import com.multithreads.management.workers.FileMerger;
import com.multithreads.management.workers.FileService;
import com.multithreads.management.workers.FileSplitter;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Future;

/**
 * Available commands.
 */
public enum Command {

    SPLIT("s", "To split file input ") {
        public List<Future<File>> apply(FileService fileService, Scanner scanner) throws IOException {
            System.out.println("Input please the complete file name(with the file path):");
            String filePath = scanner.nextLine();
            System.out.println("Input please the size in bytes of file split part:");
            String splitSize = scanner.nextLine();
            logger.info("Command '" + SPLIT.name() + "' started. The file to split: '" + filePath + "' Split part size = " + splitSize + "bytes");
            FileSplitter splitter = new FileSplitter();
            return splitter.split(filePath, splitSize, fileService);
        }
    },
    MERGE("m", "To merge file input ") {
        public List<Future<File>> apply(FileService fileService, Scanner scanner) throws IOException {
            System.out.println("Input please the directory path to merge all files from there:");
            String directoryPath = scanner.nextLine();
            logger.info("Command '" + MERGE.name() + "' started. The directory path to split files from there: '" + directoryPath + "'");
            FileMerger merger = new FileMerger();
            return merger.merge(directoryPath, fileService);
        }
    };

    /**
     * Command logger.
     */
    private final static Logger logger = Logger.getLogger(Command.class);

    /**
     * Command symbol.
     */
    private final String symbol;

    /**
     * Command message.
     */
    private final String message;

    /**
     * Initializes the symbol ant the message of the command.
     *
     * @param symbol  symbol of the command
     * @param message message of the command
     */
    Command(String symbol, String message) {
        this.symbol = symbol;
        this.message = message;
    }

    /**
     * Command action method
     *
     * @param fileService file service to assist of the command process
     * @param scanner object to receive data
     * @return list of collected Future<File>
     * @throws IOException when program goes throw IOException
     */
    public abstract List<Future<File>> apply(FileService fileService, Scanner scanner) throws IOException;

    /**
     * Print all actual command and ask user to choose the the one of them
     *
     * @param scanner object to receive data from console
     * @return actual command or goes throws exception
     * @throws InvalidCommandException when string from console doesn't mach any command symbol
     */
    public static Command chooseCommand(Scanner scanner) throws InvalidCommandException {

        for(Command command: Command.values()) {
            System.out.println(command.message + command.symbol + "\n");
        }
        String symbol = scanner.nextLine();
        for (Command command : values()) {
            if (symbol.equalsIgnoreCase(command.symbol)) {
                logger.debug("The command = '" + command.name() + "'. The command symbol = '" + symbol + "'");
                return command;
            }
        }
        logger.error("Error! Invalid command: '" + symbol + "'");
        throw new InvalidCommandException("Error! Invalid command: '" + symbol + "'");
    }
}
