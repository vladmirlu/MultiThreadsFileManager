package com.multithreads.management.task;

import com.multithreads.management.commands.Command;
import com.multithreads.management.model.FilesCurator;
import com.multithreads.management.workers.FileProvider;
import com.multithreads.statistic.StatisticService;

import org.apache.log4j.Logger;

import java.io.*;

/**
 * Task to copy bytes from one file to another.
 */
public class FileCopyist implements Runnable {

    /**
     * Service to make statistic
     */
    private final StatisticService statisticService;

    /**
     * Files data transfer object to supply file data into current task
     */
    private final FilesCurator filesCurator;

    /**
     * Object for logging the process
     */
    private final Logger logger = Logger.getLogger(FileCopyist.class);

    /**
     * Build new task
     *
     * @param filesCurator         files data transfer object
     * @param statisticService object to make statistic process
     */
    public FileCopyist(FilesCurator filesCurator, StatisticService statisticService) {

        this.filesCurator = filesCurator;
        this.statisticService = statisticService;
    }

    /**
     * Executes file copyist.
     */
    @Override
    public void run() {

        logger.info("FileCopyist started " + this);
        try {
            logger.debug("Input data the FileDTO: " + filesCurator.toString());
            RandomAccessFile fileRead = new RandomAccessFile(filesCurator.getFileRead(), "r");
            RandomAccessFile fileWrite = new RandomAccessFile(filesCurator.getFileWrite(), "rw");
            fileRead.seek(filesCurator.getReadOffset());
            fileWrite.seek(filesCurator.getWriteOffset());
            logger.debug("File to read offset = " + fileRead.getFilePointer() + "File to write offset = " + fileWrite.getFilePointer());

            long writeLength = filesCurator.getWriteLength();
            long bufferSize = FileProvider.BUFFER_SIZE;
            long completed = 0;
            long spentTime;
            while (writeLength > 0) {

                if (bufferSize >= writeLength) {
                    logger.debug("Buffer Size >= File to write length . File pointer = " + fileRead.getFilePointer() + this);
                    spentTime = copyFile(fileRead, fileWrite, writeLength);
                    completed += writeLength;
                    writeLength = 0;
                } else {
                    logger.debug("Buffer Size < File to write length. File pointer = " + fileRead.getFilePointer() + this);
                    spentTime = copyFile(fileRead, fileWrite, bufferSize);
                    writeLength -= bufferSize;
                    completed += bufferSize;
                }
                logger.info("Statistic started: copiedBytes = " + completed + "; spentNanoTime = " + spentTime);
                statisticService.trackTaskProgress(completed, Thread.currentThread().getName(), spentTime);
            }
            fileRead.close();
            fileWrite.close();
        } catch (IOException ex) {
            logger.error("IOException occur: " + ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        } finally {
            logger.info("FileCopyist completed. " + this);
        }
    }

    /**
     * Reading and writing buffer bytes from one file to another.
     *
     * @param fileRead  random access file to read bytes from itself
     * @param fileWrite random access file to write bytes into itself
     * @param bufferSize  size of transfer buffer
     * @return spent time on copying files in nanoseconds
     * @throws IOException when the files copying process crashes
     */
    private long copyFile(RandomAccessFile fileRead, RandomAccessFile fileWrite, long bufferSize) throws IOException {
        byte[] buffer = new byte[(int) bufferSize];
        long startTime = System.nanoTime();
        logger.debug("Copy file: fileReadPointer= " + fileRead.getFilePointer()+ "bytes; fileWritePointer = "+ fileWrite +  "bytes; start time = " + startTime);

        fileRead.read(buffer);
        fileWrite.write(buffer);
        fileRead.seek(fileRead.getFilePointer());
        fileWrite.seek(fileWrite.getFilePointer());

        long endTime = System.nanoTime();
        logger.debug("Copy file: fileReadPointer= " + fileRead.getFilePointer()+ "bytes; fileWritePointer = "+ fileWrite +  "bytes; end time = " + endTime);
        return endTime - startTime;
    }

    /**
     * Build override toString() method to print current task in readable format
     */
    @Override
    public String toString() {
        return new StringBuilder().append(" FileCopyist -> work data: { ")
                .append('\'').append("File fileRead: ").append(filesCurator.getFileRead())
                .append(", long readOffset = ").append(filesCurator.getReadOffset())
                .append(", File fileWrite ").append(filesCurator.getFileWrite())
                .append(", long writeOffset:").append(filesCurator.getWriteOffset())
                .append(", writeLength = ").append(filesCurator.getWriteLength())
                .append(", command = '").append(filesCurator.getWriteOffset() == 0 ? Command.SPLIT.name() : Command.MERGE.name()).append('\'')
                .append('}').toString();
    }
}
