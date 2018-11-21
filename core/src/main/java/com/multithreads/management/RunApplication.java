package com.multithreads.management;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * Main class of the application.
 */
public class RunApplication {

    /**
     * Root logger.
     */
    private final static Logger logger = Logger.getLogger(RunApplication.class);

    /**
     * Start point of the application.
     *
     * @param args arguments
     */
    public static void main(String[] args) {
        try {
            String log4jResourcesPath = "core/src/main/resources/log4j.properties";

            Communicator communicator = new Communicator();
            PropertyConfigurator.configure(log4jResourcesPath);
            logger.info("Main method started. " + communicator.toString());
            communicator.openConsole();
        } catch (Throwable e) {
            logger.error("Main method crashed " + e.getMessage());
            e.printStackTrace();
        }
    }
}
