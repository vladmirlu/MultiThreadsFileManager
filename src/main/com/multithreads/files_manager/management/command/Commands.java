package com.multithreads.files_manager.management.command;

/**
 * Available commands.
 */
public enum Commands {

    SPLIT("s"),
    MERGE("m");

    /**
     * Command symbol.
     */
    private final String symbol;

    /**
     * Initializes the symbol variable.
     *
     * @param symbol symbol of the command
     */
    Commands(String symbol) {
        this.symbol = symbol;
    }

    public final String getSymbol() {
        return this.symbol;
    }
}
