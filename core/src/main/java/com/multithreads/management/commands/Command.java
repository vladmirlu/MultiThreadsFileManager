package com.multithreads.management.commands;

import com.multithreads.management.exception.InvalidCommandException;
import com.multithreads.management.workers.FileMerger;
import com.multithreads.management.workers.FileService;
import com.multithreads.management.workers.FileSplitter;

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
            FileSplitter splitter = new FileSplitter();
            return splitter.split(filePath, splitSize, fileService);
        }
    },
    MERGE("m", "To merge file input ") {
        public List<Future<File>> apply(FileService fileService, Scanner scanner) throws IOException {
            System.out.println("Input please the directory path to merge all files from there:");
            String directoryPath = scanner.nextLine();
            FileMerger merger = new FileMerger();
            return merger.merge(directoryPath, fileService);
        }
    };

    /**
     * Command symbol.
     */
    private final String symbol;

    private final String message;

    /**
     * Initializes the symbol variable.
     *
     * @param symbol symbol of the commands
     */
    Command(String symbol, String message) {
        this.symbol = symbol;
        this.message = message;
    }

    public abstract List<Future<File>> apply(FileService fileService, Scanner scanner) throws IOException;

    public static Command chooseCommand(Scanner scanner) throws InvalidCommandException {

        System.out.println(SPLIT.message + SPLIT.symbol
                + "\n" + MERGE.message + MERGE.symbol);

        String symbol = scanner.nextLine();
        for (Command command : values()) {
            if (symbol.equalsIgnoreCase(command.symbol)) {
                return command;
            }
        }
        throw new InvalidCommandException("Error! Invalid command: '" + symbol + "'");
    }
}
