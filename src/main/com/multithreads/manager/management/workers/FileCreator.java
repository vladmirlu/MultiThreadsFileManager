package com.multithreads.manager.management.workers;

import com.multithreads.manager.management.constants.FileSizeUnit;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.List;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
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
            FileInputStream fis = new FileInputStream("src/main/resources/application.properties");
            this.RB = new PropertyResourceBundle(fis);
        }catch (MissingResourceException m){
            m.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }


    public File getDirectory(String directoryPath) {
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

        while (randomAccessFile.length() < size) {
            if (restToRead <= FileSizeUnit.BUFFER_SIZE) {
                randomAccessFile.write(new byte[(int) restToRead]);
            } else {
                randomAccessFile.write(new byte[ FileSizeUnit.BUFFER_SIZE]);
                restToRead = restToRead - FileSizeUnit.BUFFER_SIZE;
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
