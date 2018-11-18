package com.multithreads.management.task;

import com.multithreads.management.commands.Command;
import com.multithreads.management.model.FilesDTO;
import com.multithreads.management.workers.FileProvider;
import com.multithreads.statistic.StatisticService;

import org.apache.log4j.Logger;

import java.io.*;

/**
 * Transfers a certain number of bytes from one file to another.
 */
public class FileTransfer implements Runnable {

    private final StatisticService statisticService;

    private final FilesDTO filesDTO;

    private final Logger logger;


    public FileTransfer(FilesDTO filesDTO, StatisticService statisticService, Logger logger) throws IOException {

        this.filesDTO = filesDTO;
        this.statisticService = statisticService;
        this.logger = logger;
    }

    /**
     * Executes file transfer.
     */
    @Override
    public void run() {
        try {
            RandomAccessFile fileToRead = new RandomAccessFile(filesDTO.getFileToRead(), "r");
            RandomAccessFile fileToWrite = new RandomAccessFile(filesDTO.getFileToWrite(), "rw");
            fileToRead.seek(filesDTO.getFileToReadOffset());
            fileToWrite.seek(filesDTO.getFileToWriteOffset());

            logger.info("File Transfer started." + this);
            long toWriteLength = filesDTO.getFileWriteLength();
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
                statisticService.trackTaskProcess(completed, Thread.currentThread().getName(), spentTime);
            }
            fileToRead.close();
            fileToWrite.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        } finally {
            logger.trace("FileTransfer completed." + this);
        }
    }

    /**
     * Reading and writing buffer bytes from one file to another.
     *
     * @param fileToRead  randomAccessFile
     * @param fileToWrite randomAccessFile
     * @param bufferSize  size of buffer
     * @return spent time in nanoseconds
     * @throws IOException if an I/O error occurs
     */
    private long copyFile(RandomAccessFile fileToRead, RandomAccessFile fileToWrite, long bufferSize) throws IOException {
        byte[] buffer = new byte[(int) bufferSize];
        long startTime = System.nanoTime();
        logger.debug("Start Time: " + startTime + this);

        fileToRead.read(buffer);
        fileToWrite.write(buffer);
        fileToRead.seek(fileToRead.getFilePointer());
        fileToWrite.seek(fileToWrite.getFilePointer());

        long endTime = System.nanoTime();
        logger.debug("End Time: " + endTime + this);
        return endTime - startTime;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("FileTransfer { ").append("thread name: '").append(Thread.currentThread().getName())
                .append('\'').append(", file to read: ").append(filesDTO.getFileToRead())
                .append(", file to read offset = ").append(filesDTO.getFileToReadOffset())
                .append(", file to write: ").append(filesDTO.getFileToWrite())
                .append(", file to write offset:").append(filesDTO.getFileToWriteOffset())
                .append(", write file length = ").append(filesDTO.getFileWriteLength())
                .append(", user command: '").append(filesDTO.getFileToWriteOffset() == 0 ? Command.SPLIT.name() : Command.MERGE.name()).append('\'')
                .append('}').toString();
    }
}
