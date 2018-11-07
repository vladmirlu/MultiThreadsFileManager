package com.multithreads.files_manager.management.command;

import com.multithreads.files_manager.management.exception.InvalidCommandException;

/**
 * Available commands.
 */
public enum Command {
    EXIT("e"),
    SPLIT("s"),
    MERGE("m");

    /**
     * Command symbol.
     */
    private final String symbol;

    /**
     * Initializes the symbol variable.
     * @param symbol symbol of the command
     */
    Command(String symbol) {
        this.symbol = symbol;
    }

    public final String getSymbol() {
        return this.symbol;
    }

    public static Command getCommand(String symbol) throws InvalidCommandException{
        for(Command command: values()){
            if (symbol.equalsIgnoreCase(command.symbol)){
                return command;
            }
        }
        throw new InvalidCommandException("Invalid command");
    }
}
