package com.multithreads.files_manager.management.file_workers;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * File assistant tool.
 */
public class FileCreator {

    private final Logger logger;

    private ResourceBundle RB;

    FileCreator(Logger logger){
        this.logger = logger;
        try {
            RB = ResourceBundle.getBundle("../application.properties");
        }catch (MissingResourceException e){
            logger.error("Error! Could not load property file /src/main/resources/application.properties");
            e.printStackTrace();
        }
    }


    public File getDirectory(String directoryPath) throws FileNotFoundException{
      File directory =  new File(directoryPath);
      if(directory.exists()){
          return directory;
      }
      else{
          logger.warn("Parsing files in the directory path");
          return new File (RB.getString("splitFileDirectory"));
      }
    }

    public File getFile(String filePath) throws FileNotFoundException{
       File file =  new File(filePath);
       if(file.exists()){
           return file;
       }
        logger.error("Error! File " + filePath + " not found");
        throw new FileNotFoundException();
    }
    /**
     * Buffer size.
     */
    public static final int BUFFER_SIZE = 8 * 1024;

    /**
     * Name of the file that will be created after merging.
     */
    public static final String SOURCE_FILENAME = "original";
    /**
     * Creates file.
     *
     * @param filePath file path
     * @param size     file size
     * @return created file
     * @throws IOException if an I/O error occurs.
     */

    public File createFile(String filePath, long size) throws IOException {
        File file = new File(filePath);
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        long restToRead = size;
        long bufferSize = 8 * 1024;
        while (randomAccessFile.length() < size) {
            if (restToRead <= bufferSize) {
                randomAccessFile.write(new byte[(int) restToRead]);
            } else {
                randomAccessFile.write(new byte[(int) bufferSize]);
                restToRead = restToRead - bufferSize;
            }
        }
        randomAccessFile.close();

        return file;
    }

    /**
     * Calculates total size of files.
     *
     * @param files list of files
     * @return total size
     */

    public long calculateTotalSize(final List<File> files) {
        long totalSize = 0;
        for (File file : files) {
            totalSize += file.length();
        }
        return totalSize;
    }
}
