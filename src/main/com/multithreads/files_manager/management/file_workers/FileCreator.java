package com.multithreads.files_manager.management.file_workers;

import java.io.*;
import java.util.List;

/**
 * File assistant tool.
 */
public class FileCreator {


    public File getDirectory(String directoryPath) throws FileNotFoundException{
      File directory =  new File(directoryPath);
      if(directory.exists()){
          return directory;
      }
      throw new FileNotFoundException();
    }

    public File getFile(String filePath) throws FileNotFoundException{
       File file =  new File(filePath);
       if(file.exists()){
           return file;
       }
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

    public File createFile(final String filePath, final long size) throws IOException {
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
