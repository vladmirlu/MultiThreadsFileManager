package com.multithreads.management.commands;

import com.multithreads.management.exception.InvalidCommandException;
import com.multithreads.management.workers.FileMerger;
import com.multithreads.management.workers.FileService;
import com.multithreads.management.workers.FileSplitter;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Available commands.
 */
public enum Command {

    SPLIT("s", "To split file input "){

        public List<File> apply(List<String> param, FileService fileService) throws IOException {
            FileSplitter splitter = new FileSplitter();
            return splitter.split(param.get(0), param.get(1), fileService);
        }
    }
    ,
    MERGE("m", "To merge file input "){

        public List<File> apply(List<String> param, FileService fileService) throws IOException {
            FileMerger merger = new FileMerger();
           return merger.merge(param.get(0), fileService);
        }
    };

    /**
     * Command symbol.
     */
    private final String symbol;

    private final String message;

    /**
     * Initializes the symbol variable.
     * @param symbol symbol of the commands
     */
    Command(String symbol, String message) {
        this.symbol = symbol;
        this.message = message;
    }

    public abstract List<File> apply(List<String> param, FileService fileService) throws IOException;

    public static Command chooseCommand(Scanner scanner) throws InvalidCommandException {

        System.out.println(SPLIT.message + SPLIT.symbol
                + "\n" +MERGE.message + MERGE.symbol);

        String symbol = scanner.nextLine();
        for(Command command: values()){
            if (symbol.equalsIgnoreCase(command.symbol)){
                return command;
            }
        }
        throw new InvalidCommandException("Error! Invalid command!");
    }
}
