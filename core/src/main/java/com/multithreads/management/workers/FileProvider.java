package com.multithreads.management.workers;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * File assistant tool.
 */
public class FileProvider {

    /**
     * Name of the file that will be created after merging.
     */
    public static final String SOURCE_FILENAME = "original";

    /**
     * Buffer size to transfer bytes.
     */
    public static final int BUFFER_SIZE = 8 * 1024;

    /**
     * Root logger
     */
    private final Logger logger = Logger.getLogger(FileProvider.class);

    /**
     * Object to get data from the application properties file
     */
    private ResourceBundle resourceBundle;

    /**
     * Build new file provider and create resource bundle from the properties file
     */
    public FileProvider(String resourcesPath) {
        try {
            FileInputStream fis = new FileInputStream(resourcesPath);
            this.resourceBundle = new PropertyResourceBundle(fis);
        } catch (MissingResourceException m) {
            m.printStackTrace();
            logger.error("Resource is missing: " + resourcesPath + "MissingResourceException occur: " + m.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("IOException is occur during reading resources from the file '" + resourcesPath + "'; "+ e.getMessage());
        }
    }

    /**
     * Get concrete directory or default directory if input directory path not found
     *
     * @param directoryPath input directory path
     * @return directory file
     */
    public File getDirectory(String directoryPath) {
        File directory = new File(directoryPath);
        if (directory.exists()) {
            return directory;
        } else {
            logger.warn("The directory '" + directoryPath + "'not found! Parsing files in the directory of default path");
            return new File(resourceBundle.getString("splitFilesDirectory"));
        }
    }

    /**
     * Get existing file or goes throws exception if input file path not found
     *
     * @param filePath input directory path
     * @return exact file if it exists
     * @throws FileNotFoundException when file isn't exist
     */
    public File getFile(String filePath) throws FileNotFoundException {
        File file = new File(filePath);
        if (file.exists()) {
            return file;
        }
        logger.error("Error! File " + filePath + " not found");
        throw new FileNotFoundException();
    }
}
