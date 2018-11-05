package com.multithreads.files_manager.management.splitter.validator;


import com.multithreads.files_manager.management.splitter.InvalidCommandException;

/**
 * Command validation tool.
 */
public interface CommandValidator {

    /**
     * Checks if the command is valid.
     *
     * @param command input command
     * @throws InvalidCommandException in case of command invalidity
     */
    void checkCommandValidity(final String[] command) throws InvalidCommandException;

}
