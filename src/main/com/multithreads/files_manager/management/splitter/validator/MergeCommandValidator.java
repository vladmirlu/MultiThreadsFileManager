package com.multithreads.files_manager.management.splitter.validator;

import com.multithreads.files_manager.management.exception.InvalidCommandException;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

/**
 * Merge command validation tool.
 */
public class MergeCommandValidator {

    private Logger logger;

    public MergeCommandValidator(final Logger logger) {
        this.logger = logger;
    }


    public void checkCommandValidity(final String[] command) throws InvalidCommandException{
        checkCommandSignature(command);
        checkDirectoryExistence(command);
        checkEmptyDirectory(command);
    }
    /**
     * Checks if command signature is valid.
     *
     * @param command splitted command
     * @throws InvalidCommandException in case of command invalidity
     */

    public void checkCommandSignature(final String[] command) throws InvalidCommandException {
        logger.debug("Checking command signature.\nUser command: " + Arrays.toString(command));
        if (command.length != 3 || !command[1].equals("-p")) {
            throw new InvalidCommandException("Invalid command signature.");
        }
    }

    /**
     * Checks if the directory exists.
     *
     * @param command splitted command
     * @throws InvalidCommandException in case of command invalidity
     */

    public void checkDirectoryExistence(final String[] command) throws InvalidCommandException {
        logger.debug("Checking directory existence.\nUser command: " + Arrays.toString(command));
        File pathDirectory = new File(command[2]);
        if (!pathDirectory.isDirectory()) {
            throw new InvalidCommandException("Specified directory doesn't exist.");
        }
    }

    /**
     * Checks if the directory is empty.
     *
     * @param command splitted command
     * @throws InvalidCommandException in case of command invalidity
     */

    public void checkEmptyDirectory(final String[] command) throws InvalidCommandException {
        logger.debug("Checking if specified directory is empty.\nUser command: " + Arrays.toString(command));
        File directory = new File(command[2]);
        if (Objects.requireNonNull(directory.listFiles()).length == 0) {
            throw new InvalidCommandException("Empty directory.");
        }
    }
}
