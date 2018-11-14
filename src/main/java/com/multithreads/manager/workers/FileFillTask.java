package com.multithreads.manager.workers;

import com.multithreads.manager.constants.FileSizeUnit;
import com.multithreads.manager.model.FilesDTO;
import statistics.StatisticService;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.concurrent.Callable;

/**
 * Transfers a certain number of bytes from one file to another.
 */
public class FileFillTask implements Callable {

    private final StatisticService statisticService;

    private final FilesDTO filesDTO;

    private final RandomAccessFile fileToRead;

    private final RandomAccessFile fileToWrite;

    private final Logger logger;


    public FileFillTask(FilesDTO filesDTO, StatisticService statisticService, Logger logger) throws IOException {

        this.filesDTO = filesDTO;
        this.fileToRead = new RandomAccessFile(filesDTO.getFileToRead(), "r");
        this.fileToWrite = new RandomAccessFile(filesDTO.getFileToWrite(), "rw");
        this.fileToRead.seek(filesDTO.getFileToReadOffset());
        this.fileToWrite.seek(filesDTO.getFileToWriteOffset());
        this.statisticService = statisticService;
        this.logger = logger;
    }

    /**
     * Executes transfer.
     */
    @Override
    public File call() {
        try {
            final String threadName = Thread.currentThread().getName();
            logger.info("FileFillTask started." + this);
            long toWriteLength = filesDTO.getFileWriteLength();
            final long bufferSize = toWriteLength <= filesDTO.getFileToRead().length() ? FileSizeUnit.getSpecificBufferSize(toWriteLength) : FileSizeUnit.getSpecificBufferSize(filesDTO.getFileToRead().length());
            long completed = 0;
            long time;
            while (fileToRead.getFilePointer() - filesDTO.getFileToReadOffset() < toWriteLength) {

                if (bufferSize >= toWriteLength) {
                    logger.debug("Buffer Size >= File to write length . FilePointer: " + fileToRead.getFilePointer() + this);
                    time = copyFileAndGetSpentTime(fileToRead, fileToWrite, toWriteLength);
                    completed += toWriteLength;
                } else {
                    logger.debug("Buffer Size < File to write length. FilePointer: " + fileToRead.getFilePointer() + this);
                    time = copyFileAndGetSpentTime(fileToRead, fileToWrite, bufferSize);
                    toWriteLength -= bufferSize;
                    completed += bufferSize;
                }
                try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e.getMessage());
            }
                statisticService.trackTaskProcess(completed,  threadName,  filesDTO.getFileWriteLength(), time);
            }

            return filesDTO.getFileToWrite();

        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        } finally {
            logger.trace("FileFillTask completed." + this);
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
    private long copyFileAndGetSpentTime(RandomAccessFile fileToRead, RandomAccessFile fileToWrite, long bufferSize) throws IOException {
        byte[] buffer = new byte[(int) bufferSize];
        long startTime = System.nanoTime();
        logger.debug("StartTime: " + startTime + this);
        fileToRead.read(buffer);
        fileToWrite.write(buffer);
        fileToRead.close();
        fileToWrite.close();
        long endTime = System.nanoTime();
        logger.debug("EndTime: " + endTime + this);
        return endTime - startTime;
    }
}
