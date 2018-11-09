package com.multithreads.files_manager.management.file_workers;

import com.multithreads.files_manager.management.model.FileData;
import com.multithreads.files_manager.statistics.StatisticService;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
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
    private final  ExecutorService fileWorkersPool;

    /**
     * File assistant tool.
     */
    private final FileCreator fileCreator;

    public FileService(Logger logger) {
        this.logger = logger;
        this.fileCreator  = new FileCreator(logger);
        this.fileWorkersPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public Future<File> getWorkerFuture(File parentFile, long length,  long fromFileOffset, long toFileOffset,  File childFile,  StatisticService statService){
        FileData fileData = new FileData(parentFile, childFile, fromFileOffset, toFileOffset, length);
        return fileWorkersPool.submit(new FileTransfer(fileData, statService), childFile);
    }

    public File getOriginalFile(List<File> files) throws IOException {
        long totalSize = fileCreator.calculateTotalSize(files);
        String originalFilePath = files.get(0).getParent() + "/" + FileCreator.SOURCE_FILENAME + "." + FilenameUtils.getExtension(files.get(0).getName());
        return fileCreator.createFile(originalFilePath, totalSize);
    }

    public List<File> getSplitFilesList(String directoryPath) throws FileNotFoundException{
        logger.debug("Parsing files in the directory path");
        File directory = fileCreator.getDirectory(directoryPath);
        File[] files = directory.listFiles();
        if(files != null)
        return Arrays.asList(files);
        logger.error("Error! Not found files to merge in the directory "+ directory.getName());
        throw new FileNotFoundException();
    }

    public long getFilePartSize(File file, String splitFileSize) throws FileNotFoundException{

        long splitSize = Long.parseLong(splitFileSize);
        if(file.length() <= splitSize){
         return file.length();
        }
       else {
           long bytesLeft = file.length() % splitSize;
           long partsQuantity = (file.length() - bytesLeft) / splitSize;
           return (file.length() - bytesLeft) / partsQuantity;
        }
    }

    public ExecutorService getFileWorkersPool() {
        return fileWorkersPool;
    }

    public FileCreator getFileCreator() {
        return fileCreator;
    }
}
