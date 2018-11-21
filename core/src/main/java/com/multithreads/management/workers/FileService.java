package com.multithreads.management.workers;

import com.multithreads.management.model.FilesDTO;
import com.multithreads.management.task.FileCopyist;
import com.multithreads.statistic.StatisticService;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FileService {

    /**
     * Root logger.
     */
    private final Logger logger = Logger.getLogger(FileService.class);

    /**
     * File workers thread pool.
     */
    private final ExecutorService fileWorkersPool;

    /**
     * service to make statistic
     */
    private final StatisticService statisticService;

    /**
     * File assistant tool.
     */
    private final FileProvider fileProvider;

    /**
     * Build service of multi threads files management
     *
     */
    public FileService(String resourcePath) {

        this.fileProvider = new FileProvider(resourcePath);
        this.statisticService = new StatisticService(resourcePath);
        this.fileWorkersPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.logger.debug("Create Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() {'" + this.fileWorkersPool.toString() + "}'");
    }

    /**
     * Create and start new task Future<File>
     *
     * @param fileToRead    file to read bytes from
     * @param writeLength   writing file length
     * @param toReadOffset  pointer offset in the file from which to transfer the bytes
     * @param toWriteOffset pointer offset in the file to write transfer the bytes
     * @param fileToWrite   file to write bytes into
     * @return new created task Future<File>
     */
    public Future<File> createTaskFuture(File fileToRead, long writeLength, long toReadOffset, long toWriteOffset, File fileToWrite) {
        FilesDTO filesDTO = new FilesDTO(fileToRead, fileToWrite, toReadOffset, toWriteOffset, writeLength);
        logger.debug("Submit new task" + filesDTO.toString() + " in work thread pool");
        return fileWorkersPool.submit(new FileCopyist(filesDTO, statisticService), filesDTO.getFileWrite());
    }

    /**
     * Create clone of the original file from all split file parts
     *
     * @param files list of split file parts
     * @return new created clone-file of original file
     */
    public File createOriginalFileClone(List<File> files) {
        String originalFilePath = files.get(0).getParent() + "/" + FileProvider.SOURCE_FILENAME + "." + FilenameUtils.getExtension(files.get(0).getName());
        statisticService.initStatistic(calculateTotalSize(files));
        logger.debug("Create original file '" + originalFilePath + "' and set total size of " + calculateTotalSize(files) + "bytes in statistic");
        return new File(originalFilePath);
    }

    /**
     * find existing file by the file path or goes throws exception
     *
     * @param filePath input file path
     * @return exact existing file
     * @throws FileNotFoundException when file does'nt exist
     */
    public File findExistingFile(String filePath) throws FileNotFoundException {
        File file = fileProvider.getFile(filePath);
        statisticService.initStatistic(file.length());
        logger.debug("Find some file '" + file.getName() + "' and set the file length(" + file.length() + " bytes) as total size in statistic");
        return file;
    }

    /**
     * find list of all split parts from concrete directory or goes throws exception
     *
     * @param directoryPath input directory path
     * @return list of all split parts from directory
     * @throws FileNotFoundException when the directory does'nt exist
     */
    public List<File> findSplitFilesList(String directoryPath) throws FileNotFoundException {
        logger.debug("Parsing files in the directory of path: '" + directoryPath + "'");
        File directory = fileProvider.getDirectory(directoryPath);
        File[] files = directory.listFiles();
        if (files != null) {
            return Arrays.asList(files);
        }
        logger.error("Error! Not found files to merge in the directory " + directory.getName());
        throw new FileNotFoundException();
    }

    /**
     * Shutdowns work and statistic thread pools
     */
    public void shutdownThreadPools() {
        logger.info("Shutdown work thread pool: {" + fileWorkersPool.toString() + "} and statistic thread pool: {" + statisticService.getStatisticsPool() + "}");
        fileWorkersPool.shutdown();
        statisticService.getStatisticsPool().shutdown();
    }

    /**
     * Calculates total size of files.
     *
     * @param files list of files
     * @return files common length in bytes
     */
    public long calculateTotalSize(List<File> files) {
        long totalSize = 0;
        for (File file : files) {
            totalSize += file.length();
        }
        logger.debug("Calculate total file size from List<File> files. Files quantity= '" + files.size() + "Total file size = " + totalSize + "bytes'");
        return totalSize;
    }
/**
 * Turn on statistic printing
 * */
    public void turnOnStatisticPrint(){
        statisticService.printStatistic();
    }

    /**
     *Get readable string of this object
     *
     * @return object as string
     * */
    @Override
    public String toString(){
        return  new StringBuilder().append("FileService: { ").append(this.getClass()).append(", hashCode= ").append(this.hashCode()).toString();
    }
}
