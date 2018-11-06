package com.multithreads.files_manager.management;

import com.multithreads.files_manager.management.splitter.FileAssistant;
import com.multithreads.files_manager.management.splitter.Transfer;
import com.multithreads.files_manager.management.splitter.provider.PropertiesProvider;
import com.multithreads.files_manager.statistics.ProgressPrinter;
import com.multithreads.files_manager.statistics.TaskTracker;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FileService {

    /**
     * Root logger.
     */
    private Logger logger;

    /**
     * File workers thread pool.
     */
    private  ExecutorService fileWorkersPool;

    /**
     * Statistics thread pool.
     */
    private ExecutorService statisticsPool;

    /**
     * Tool for providing file properties.
     */
    private final PropertiesProvider propertiesProvider;

    /**
     * Interface for interaction with the src.main.com.multithreads.files_manager.statistics module.
     */
    private TaskTracker taskTracker;

    /**
     * File assistant tool.
     */
    private FileAssistant fileAssistant;

    public FileService(PropertiesProvider propertiesProvider, TaskTracker taskTracker, Logger logger){
        this.fileWorkersPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.statisticsPool  =  Executors.newFixedThreadPool(1);
        this.propertiesProvider = propertiesProvider;
        this.fileAssistant  = new FileAssistant();
        this.taskTracker = taskTracker;
        this.logger = logger;
    }

    public Future<?> getWorkerFuture(File file, long length,  long fromFileOffset, long toFileOffset,  File originalFile){
        return fileWorkersPool.submit(
                new Transfer(file, fromFileOffset, length, originalFile, toFileOffset, propertiesProvider,
                        taskTracker));
    }

    public File getOriginalFile(List<File> files) throws IOException {
        long totalSize = fileAssistant.calculateTotalSize(files);
        taskTracker.setTotalTasks(totalSize);
        String originalFilePath = files.get(0).getParent() + "/" + propertiesProvider.SOURCE_FILENAME + "."
                + FilenameUtils.getExtension(files.get(0).getName());
        return fileAssistant.createFile(originalFilePath, totalSize);
    }

    public List<File> parseFiles(final String[] args) {
        logger.debug("Parsing files in the directory path. User command: " + Arrays.toString(args));
        String pathDirectory = args[2];
        File directory = new File(pathDirectory);
        File[] files = directory.listFiles();

        return Arrays.asList(files);
    }

    public void setStatistic(List<Future<?>> futures) throws InterruptedException, ExecutionException {
        Future<?> f = statisticsPool.submit(new ProgressPrinter(taskTracker));
        futures.add(f);
        for (Future<?> future : futures) {
            future.get();
        }

        taskTracker.setTotalTasks(0);
        taskTracker.setCompletedTasks(0);
        taskTracker.getReportsPerSection().clear();
    }

    public FileAssistant getFileAssistant() {
        return fileAssistant;
    }
}
