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

    public FileService(Logger logger) {
        this.logger = logger;
        this.fileProvider = new FileProvider(logger);
        this.statisticService = new StatisticService();
        this.fileWorkersPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.logger.debug("Create Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() {'" + this.fileWorkersPool.toString() + "}'");
    }

    public Future<File> getWorkerFuture(File parentFile, long length, long fromFileOffset, long toFileOffset, File childFile) throws IOException {
        FilesDTO filesDTO = new FilesDTO(parentFile, childFile, fromFileOffset, toFileOffset, length);
        logger.debug("Submit new task in work thread pool to read data from '" +parentFile.getName() + "' and write into '" + childFile.getName() + "'");
        return fileWorkersPool.submit(new FileTransfer(filesDTO, statisticService, logger), filesDTO.getFileToWrite());
    }

    public File createOriginalFile(List<File> files) throws IOException {
        long totalSize = calculateTotalSize(files);
        statisticService.initStatistic(totalSize);
        String originalFilePath = files.get(0).getParent() + "/" + FileProvider.SOURCE_FILENAME + "." + FilenameUtils.getExtension(files.get(0).getName());
        logger.debug("Create original file '" + originalFilePath  + "' and set the file length("+ totalSize +" bytes) as total size in statistic");
        return fileProvider.createFile(originalFilePath, totalSize);
    }

    public File getOriginalFile(String filePath) throws FileNotFoundException{
        File file = fileProvider.getFile(filePath);
        statisticService.initStatistic(file.length());
        logger.debug("Find some file '" + file.getName() + "' and set the file length("+ file.length() +" bytes) as total size in statistic");
        return file;
    }

    public List<File> getSplitFilesList(String directoryPath) throws FileNotFoundException {
        logger.debug("Parsing files in the directory of path: '" + directoryPath + "'");
        File directory = fileProvider.getDirectory(directoryPath);
        File[] files = directory.listFiles();
        if (files != null) {
            return Arrays.asList(files);
        }
        logger.error("Error! Not found files to merge in the directory " + directory.getName());
        throw new FileNotFoundException();
    }

    public void shutdownThreadPools() {
        logger.info("Shutdown work thread pool: {" + fileWorkersPool.toString() + "} and statistic thread pool: {" + statisticService.getStatisticsPool()+ "}" );
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
        logger.debug("Calculate total file size from List<File> files. Files quantity= '" + files.size() + "Total file size = " + totalSize + "bytes'");
        return totalSize;
    }
}
