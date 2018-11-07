package com.multithreads.files_manager.management.file_workers;

import com.multithreads.files_manager.statistics.TaskTracker;
import com.multithreads.files_manager.statistics.ProgressPrinter;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FileService {

    /**
     * Root logger.
     */
    private final Logger logger;

    /**
     * File workers thread pool.
     */
    private final  ExecutorService fileWorkersPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    /**
     * File assistant tool.
     */
    private final FileCreator fileCreator = new FileCreator();

    public ResourceBundle RB;

    public FileService(Logger logger) {
        this.logger = logger;
        try {
            FileInputStream fis = new FileInputStream("src/main/resources/application.properties");
            this.RB = new PropertyResourceBundle(fis);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public Future<?> getWorkerFuture(File file, long length,  long fromFileOffset, long toFileOffset,  File originalFile){
        return fileWorkersPool.submit(
                new FileTransfer(file, fromFileOffset, length, originalFile, toFileOffset));
    }

    public File getOriginalFile(List<File> files) throws IOException {
        long totalSize = fileCreator.calculateTotalSize(files);
        String originalFilePath = files.get(0).getParent() + "/" + FileCreator.SOURCE_FILENAME + "."
                + FilenameUtils.getExtension(files.get(0).getName());
        return fileCreator.createFile(originalFilePath, totalSize);
    }

    public List<File> parseFiles() throws FileNotFoundException{
        logger.debug("Parsing files in the directory path");
        File directory = new File(RB.getString("splitFileDirectory"));
        File[] files = directory.listFiles();
        if(files != null)
        return Arrays.asList(files);
        throw new FileNotFoundException();
    }

    public long parseSize() {
    File file = new File(RB.getString("filePath"));
        long size = file.length();
        /*for (FileSizeUnit sizeUnit : FileSizeUnit.values()) {
            if (String.valueOf(size).endsWith(String.valueOf(sizeUnit))) {
                size = Long.parseLong(String.valueOf(size).substring(0, String.valueOf(size).indexOf(String.valueOf(sizeUnit))))
                        * sizeUnit.getCoefficient();
            }
        }*/
        return size / 5;
    }

    public ExecutorService getFileWorkersPool() {
        return fileWorkersPool;
    }

    public FileCreator getFileCreator() {
        return fileCreator;
    }
}
