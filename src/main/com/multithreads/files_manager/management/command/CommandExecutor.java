package com.multithreads.files_manager.management.command;

import com.multithreads.files_manager.management.exception.InvalidCommandException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class CommandExecutor {

    private Logger logger;

    private SplitCommand splitCommand;

    private MergeCommand mergeCommand;

    public CommandExecutor(Logger logger, SplitCommand splitCommand,
                           MergeCommand mergeCommand) {
        this.logger = logger;
        this.splitCommand = splitCommand;
        this.mergeCommand = mergeCommand;
    }

    /**
     * Executes the input command.
     *
     * @param commandStr command
     * @throws IOException             if an I/O error occurs
     * @throws ExecutionException      if the computation threw an exception
     * @throws InterruptedException    in case of thread interrupting
     * @throws InvalidCommandException in case of command invalidity
     */
    public void execute(final String commandStr) throws IOException, ExecutionException, InterruptedException,
            InvalidCommandException {
        String[] args = commandStr.split(" ");
        logger.debug("Splitted command: " + Arrays.toString(args));

        switch (Commands.valueOf(args[0].toUpperCase())) {
            case SPLIT:
                splitCommand.execute(args);
                break;
            case MERGE:
                mergeCommand.execute(args);
                break;
        }
    }
}
