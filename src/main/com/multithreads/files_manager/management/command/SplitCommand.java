package com.multithreads.files_manager.management.command;

import com.multithreads.files_manager.management.exception.InvalidCommandException;
import com.multithreads.files_manager.management.splitter.Transfer;
import com.multithreads.files_manager.management.splitter.parser.SplitParamParser;
import com.multithreads.files_manager.management.splitter.provider.PropertiesProvider;
import com.multithreads.files_manager.management.splitter.validator.SplitCommandValidator;
import com.multithreads.files_manager.statistics.ProgressPrinter;
import com.multithreads.files_manager.statistics.TaskTracker;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Split command.
 */
public class SplitCommand  {

    /**
     * Root logger.
     */
    private Logger logger;

    /**
     * Split params parser.
     */
    private SplitParamParser splitParamParser;

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
    private SplitCommandValidator splitCommandValidator;

    public SplitCommand(final Logger logger, final SplitParamParser splitParamParser,
                        final PropertiesProvider propertiesProvider, final ExecutorService fileWorkersPool,
                        final ExecutorService statisticsPool, final TaskTracker taskTracker,
                        final SplitCommandValidator splitCommandValidator) {
        this.logger = logger;
        this.splitParamParser = splitParamParser;
        this.propertiesProvider = propertiesProvider;
        this.fileWorkersPool = fileWorkersPool;
        this.statisticsPool = statisticsPool;
        this.taskTracker = taskTracker;
        this.splitCommandValidator = splitCommandValidator;
    }

    /**
     * Splits file.
     *
     * @param strings command arguments
     * @return list of files
     * @throws ExecutionException      if the computation threw an exception
     * @throws InterruptedException    in case of thread interrupting
     * @throws InvalidCommandException in case of command invalidity
     * @throws IOException             if an I/O error occurs
     */

    public List<File> execute(final String[] strings) throws IOException, ExecutionException, InterruptedException,
            InvalidCommandException {

        splitCommandValidator.checkCommandValidity(strings);
        String userCommandStr = "\nUser command: " + Arrays.toString(strings);
        File file = new File(splitParamParser.parsePath(strings));
        long partSize = splitParamParser.parseSize(strings);
        long fileSize = file.length();
        long numSplits = fileSize / partSize;
        long remainingBytes = fileSize % partSize;
        taskTracker.setTotalTasks(fileSize);

        List<Future<?>> futures = new ArrayList<>();
        List<File> files = new ArrayList<>();

        Files.createDirectory(Paths.get(file.getParent() + "/parts"));
        for (long i = 0; i < numSplits; i++) {
            File partFile = new File(file.getParent() + "/parts/" + i + "."
                                             + FilenameUtils.getExtension(file.getName()));
            Future<?> f = fileWorkersPool.submit(new Transfer(file, i * partSize, partSize, partFile, 0,
                                                              propertiesProvider, taskTracker, userCommandStr));
            futures.add(f);
            files.add(partFile);
        }
        if (remainingBytes > 0) {
            logger.debug("Remaining bytes > 0. One additional file will be added." + userCommandStr);
            File partFile = new File(file.getParent() + "/parts/" + (numSplits) + "."
                                             + FilenameUtils.getExtension(file.getName()));
            Future<?> f = fileWorkersPool.submit(
                    new Transfer(file, fileSize - remainingBytes, remainingBytes, partFile, 0,
                                 propertiesProvider, taskTracker, userCommandStr));
            futures.add(f);
            files.add(partFile);
        }

        Future<?> f = statisticsPool.submit(new ProgressPrinter(taskTracker, Arrays.toString(strings)));
        futures.add(f);
        for (Future<?> future : futures) {
            future.get();
        }
        logger.debug("Splitting completed." + userCommandStr);
        taskTracker.setTotalTasks(0);
        taskTracker.setCompletedTasks(0);
        taskTracker.getReportsPerSection().clear();


        return files;
    }
}
