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
     * @param args arguments
     */
    public static void main(String[] args) {
            try {
                Communicator communicator = new Communicator(logger);
                PropertyConfigurator.configure("core/src/main/resources/application.properties");
                logger.info("Main method started. " + communicator.toString());
                communicator.openConsole();
            }
            catch (Exception e){
                e.printStackTrace();
            }

    }
}
