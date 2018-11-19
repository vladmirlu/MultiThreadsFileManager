package com.multithreads.management.task;

import com.multithreads.management.commands.Command;
import com.multithreads.management.model.FilesDTO;
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
    private final FilesDTO filesDTO;

    /**
     * Root logger
     */
    private final Logger logger;

    /**
     * Build new task
     *
     * @param filesDTO         files data transfer object
     * @param statisticService object to make statistic process
     * @param logger           object to write logs during the time when the task is running
     */
    public FileCopyist(FilesDTO filesDTO, StatisticService statisticService, Logger logger) {

        this.filesDTO = filesDTO;
        this.statisticService = statisticService;
        this.logger = logger;
    }

    /**
     * Executes file copyist.
     */
    @Override
    public void run() {
        try {
            RandomAccessFile fileToRead = new RandomAccessFile(filesDTO.getFileToRead(), "r");
            RandomAccessFile fileToWrite = new RandomAccessFile(filesDTO.getFileToWrite(), "rw");
            fileToRead.seek(filesDTO.getToReadOffset());
            fileToWrite.seek(filesDTO.getToWriteOffset());

            logger.info("FileCopyist started " + this);
            long toWriteLength = filesDTO.getWriteLength();
            long bufferSize = FileProvider.BUFFER_SIZE;
            long completed = 0;
            long spentTime;
            while (toWriteLength > 0) {

                if (bufferSize >= toWriteLength) {
                    logger.debug("Buffer Size >= File to write length . File pointer = " + fileToRead.getFilePointer() + this);
                    spentTime = copyFile(fileToRead, fileToWrite, toWriteLength);
                    completed += toWriteLength;
                    toWriteLength = 0;
                } else {
                    logger.debug("Buffer Size < File to write length. File pointer = " + fileToRead.getFilePointer() + this);
                    spentTime = copyFile(fileToRead, fileToWrite, bufferSize);
                    toWriteLength -= bufferSize;
                    completed += bufferSize;
                }
                statisticService.trackTasksProgress(completed, Thread.currentThread().getName(), spentTime);
            }
            fileToRead.close();
            fileToWrite.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        } finally {
            logger.trace("FileCopyist completed." + this);
        }
    }

    /**
     * Reading and writing buffer bytes from one file to another.
     *
     * @param fileToRead  random access file to read bytes from itself
     * @param fileToWrite random access file to write bytes into itself
     * @param bufferSize  size of transfer buffer
     * @return spent time on copying files in nanoseconds
     * @throws IOException when the files copying process crashes
     */
    private long copyFile(RandomAccessFile fileToRead, RandomAccessFile fileToWrite, long bufferSize) throws IOException {
        byte[] buffer = new byte[(int) bufferSize];
        long startTime = System.nanoTime();
        logger.debug("Copy file: start time = " + startTime + this);

        fileToRead.read(buffer);
        fileToWrite.write(buffer);
        fileToRead.seek(fileToRead.getFilePointer());
        fileToWrite.seek(fileToWrite.getFilePointer());

        long endTime = System.nanoTime();
        logger.debug("Copy file: end time = " + endTime + this);
        return endTime - startTime;
    }

    /**
     * Build override toString() method to print current task in readable format
     */
    @Override
    public String toString() {
        return new StringBuilder().append("FileCopyist { ").append("thread name: '").append(Thread.currentThread().getName())
                .append('\'').append(", file to read: ").append(filesDTO.getFileToRead())
                .append(", file to read offset = ").append(filesDTO.getToReadOffset())
                .append(", file to write: ").append(filesDTO.getFileToWrite())
                .append(", file to write offset:").append(filesDTO.getToWriteOffset())
                .append(", write file length = ").append(filesDTO.getWriteLength())
                .append(", user command: '").append(filesDTO.getToWriteOffset() == 0 ? Command.SPLIT.name() : Command.MERGE.name()).append('\'')
                .append('}').toString();
    }
}
