package com.multithreads.files_manager.management.splitter;

/**
 * Exception that is thrown in case of command invalidity.
 */
public class InvalidCommandException extends Exception {

    /**
     * Initializes message variable in the superclass.
     *
     * @param message exception message
     */
    public InvalidCommandException(String message) {
        super(message);
    }
}
