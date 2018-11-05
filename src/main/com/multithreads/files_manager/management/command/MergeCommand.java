package com.multithreads.files_manager.management.command;

import com.multithreads.files_manager.management.exception.InvalidCommandException;
import com.multithreads.files_manager.management.splitter.FileAssistant;
import com.multithreads.files_manager.management.splitter.Transfer;
import com.multithreads.files_manager.management.splitter.parser.MergeParamParser;
import com.multithreads.files_manager.management.splitter.provider.PropertiesProvider;
import com.multithreads.files_manager.management.splitter.validator.MergeCommandValidator;
import com.multithreads.files_manager.statistics.ProgressPrinter;
import com.multithreads.files_manager.statistics.TaskTracker;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Merge command.
 */
public class MergeCommand {

    /**
     * Root logger.
     */
    private Logger logger;

    /**
     * File assistant tool.
     */
    private FileAssistant fileAssistant;

    /**
     * Merge params parser.
     */
    private MergeParamParser mergeParamParser;

    /**
     * Tool for providing file properties.
     */
    private PropertiesProvider propertiesProvider;

    /**
     * File workers thread pool.
     */
    private ExecutorService fileWorkersPool;

    /**
     * Statistics thread pool.
     */
    private ExecutorService statisticsPool;

    /**
     * Interface for interaction with the src.main.com.multithreads.files_manager.statistics module.
     */
    private TaskTracker taskTracker;

    /**
     * Command validation tool.
     */
    private MergeCommandValidator mergeCommandValidator;

    public MergeCommand(final Logger logger, final FileAssistant fileAssistant,
                        final MergeParamParser mergeParamParser,
                        final PropertiesProvider propertiesProvider, final ExecutorService fileWorkersPool,
                        final ExecutorService statisticsPool, final TaskTracker taskTracker,
                        final MergeCommandValidator mergeCommandValidator) {
        this.logger = logger;
        this.fileAssistant = fileAssistant;
        this.mergeParamParser = mergeParamParser;
        this.propertiesProvider = propertiesProvider;
        this.fileWorkersPool = fileWorkersPool;
        this.statisticsPool = statisticsPool;
        this.taskTracker = taskTracker;
        this.mergeCommandValidator = mergeCommandValidator;
    }

    /**
     * Merges files.
     *
     * @param args command arguments
     * @return list with merged file
     * @throws ExecutionException      if the computation threw an exception
     * @throws InterruptedException    in case of thread interrupting
     * @throws IOException             if an I/O error occurs
     */
    public List<File> execute(final String[] args) throws IOException, ExecutionException, InterruptedException, InvalidCommandException  {
        mergeCommandValidator.checkCommandValidity(args);
        String userCommandStr = "\nUser command: " + Arrays.toString(args);
        List<File> files = mergeParamParser.parseFiles(args);
        long totalSize = fileAssistant.calculateTotalSize(files);
        taskTracker.setTotalTasks(totalSize);
        String originalFilePath = files.get(0).getParent() + "/" + propertiesProvider.SOURCE_FILENAME + "."
                + FilenameUtils.getExtension(files.get(0).getName());
        File originalFile = fileAssistant.createFile(originalFilePath, totalSize);


        files.sort(Comparator.comparingInt(o -> Integer.parseInt(FilenameUtils.getBaseName(o.getName()))));

        long iterations = files.get(files.size() - 1).length() < files.get(0).length() ? files.size() - 1 :
                files.size();
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < iterations; i++) {
            long num = Integer.parseInt(FilenameUtils.getBaseName(files.get(i).getName()));
            Future<?> f = fileWorkersPool.submit(
                    new Transfer(files.get(i), 0, files.get(i).length(), originalFile,
                                 num * files.get(i).length(), propertiesProvider,
                                 taskTracker, userCommandStr));
            futures.add(f);
        }
        if (iterations == files.size() - 1) {
            Future<?> f = fileWorkersPool.submit(
                    new Transfer(files.get(files.size() - 1), 0, files.get(files.size() - 1).length(),
                                 originalFile, totalSize - files.get(files.size() - 1).length(),
                                 propertiesProvider, taskTracker, userCommandStr));
            futures.add(f);
        }

        Future<?> f = statisticsPool.submit(new ProgressPrinter(taskTracker, Arrays.toString(args)));
        futures.add(f);
        for (Future<?> future : futures) {
            future.get();
        }

        taskTracker.setTotalTasks(0);
        taskTracker.setCompletedTasks(0);
        taskTracker.getReportsPerSection().clear();


        List<File> originalFiles = new ArrayList<>();
        originalFiles.add(originalFile);

        return originalFiles;
    }
}
