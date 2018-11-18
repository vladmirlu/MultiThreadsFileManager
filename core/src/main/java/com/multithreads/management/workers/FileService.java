package com.multithreads.management.workers;

import com.multithreads.management.model.FilesDTO;
import com.multithreads.management.task.FileTransfer;
import com.multithreads.statistic.StatisticService;

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
    private final ExecutorService fileWorkersPool;

    private final StatisticService statisticService;

    /**
     * File assistant tool.
     */
    private final FileProvider fileProvider;

    public FileService() {
        this.logger = Logger.getRootLogger();
        this.fileProvider = new FileProvider(logger);
        this.statisticService = new StatisticService();
        this.fileWorkersPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public Future<File> getWorkerFuture(File parentFile, long length, long fromFileOffset, long toFileOffset, File childFile) throws IOException {
        FilesDTO filesDTO = new FilesDTO(parentFile, childFile, fromFileOffset, toFileOffset, length);
        return fileWorkersPool.submit(new FileTransfer(filesDTO, statisticService, logger), filesDTO.getFileToWrite());
    }

    public File createOriginalFile(List<File> files) throws IOException {
        long totalSize = calculateTotalSize(files);
        statisticService.initStatistic(totalSize);
        String originalFilePath = files.get(0).getParent() + "/" + FileProvider.SOURCE_FILENAME + "." + FilenameUtils.getExtension(files.get(0).getName());
        return fileProvider.createFile(originalFilePath, totalSize);
    }

    public File getOriginalFile(String filePath) throws FileNotFoundException{
        File file = fileProvider.getFile(filePath);
        statisticService.initStatistic(file.length());
        return file;
    }

    public List<File> getSplitFilesList(String directoryPath) throws FileNotFoundException {
        logger.debug("Parsing files in the directory path");
        File directory = fileProvider.getDirectory(directoryPath);
        File[] files = directory.listFiles();
        if (files != null) {
            return Arrays.asList(files);
        }
        logger.error("Error! Not found files to merge in the directory " + directory.getName());
        throw new FileNotFoundException();
    }

    public void shutdownThreadPools() {
        fileWorkersPool.shutdown();
        statisticService.getStatisticsPool().shutdown();
    }

    /**
     * Calculates total size of files.
     *
     * @param files list of files
     * @return total size
     */

    public long calculateTotalSize(List<File> files) {
        long totalSize = 0;
        for (File file : files) {
            totalSize += file.length();
        }
        return totalSize;
    }
}
