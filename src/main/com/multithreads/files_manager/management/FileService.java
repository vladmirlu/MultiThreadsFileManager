package com.multithreads.files_manager.management;

import com.multithreads.files_manager.management.splitter.FileAssistant;
import com.multithreads.files_manager.management.splitter.SizeUnits;
import com.multithreads.files_manager.management.splitter.TaskTrackerImpl;
import com.multithreads.files_manager.management.splitter.Transfer;
import com.multithreads.files_manager.management.splitter.provider.PropertiesProvider;
import com.multithreads.files_manager.statistics.ProgressPrinter;
import com.multithreads.files_manager.statistics.TaskTracker;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
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
     * Statistics thread pool.
     */
    private final ExecutorService statisticsPool = Executors.newFixedThreadPool(1);

    /**
     * Tool for providing file properties.
     */
    private final PropertiesProvider propertiesProvider = new PropertiesProvider();

    /**
     * Interface for interaction with the src.main.com.multithreads.files_manager.statistics module.
     */
    private final TaskTracker taskTracker = new TaskTrackerImpl();

    /**
     * File assistant tool.
     */
    private final FileAssistant fileAssistant = new FileAssistant();

    public static ResourceBundle rb = ResourceBundle.getBundle("application");

    public FileService(Logger logger){
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

    public List<File> parseFiles() throws FileNotFoundException{
        logger.debug("Parsing files in the directory path");
        File directory = new File(rb.getString("splitFileDidectory"));
        File[] files = directory.listFiles();
        if(files != null)
        return Arrays.asList(files);
        throw new FileNotFoundException();
    }

    public long parseSize(String sizeStr) {

        long size = Long.parseLong(sizeStr);
        for (SizeUnits sizeUnit : SizeUnits.values()) {
            if (sizeStr.endsWith(String.valueOf(sizeUnit))) {
                size = Long.parseLong(sizeStr.substring(0, sizeStr.indexOf(String.valueOf(sizeUnit))))
                        * sizeUnit.getCoefficient();
            }
        }
        return size;
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

    public void shutDownTreads(){
        fileWorkersPool.shutdown();
        statisticsPool.shutdown();
    }

    public FileAssistant getFileAssistant() {
        return fileAssistant;
    }
}
