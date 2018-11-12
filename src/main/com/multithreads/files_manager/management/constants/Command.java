package com.multithreads.files_manager.management.constants;

import com.multithreads.files_manager.management.exception.InvalidCommandException;

/**
 * Available commands.
 */
public enum Command {

    SPLIT("s", "To split file input "),
    MERGE("m", "To merge file input "),
    EXIT("e", "To exit input ");

    /**
     * Command symbol.
     */
    private final String symbol;

    private final String message;

    /**
     * Initializes the symbol variable.
     * @param symbol symbol of the constants
     */
    Command(String symbol, String message) {
        this.symbol = symbol;
        this.message = message;
    }

    public final String getSymbol() {
        return this.symbol;
    }

    public String getMessage() {
        return message;
    }

    public static Command getCommand(String symbol) throws InvalidCommandException{
        for(Command command: values()){
            if (symbol.equalsIgnoreCase(command.symbol)){
                return command;
            }
        }
        throw new InvalidCommandException("Invalid constants");
    }
}
