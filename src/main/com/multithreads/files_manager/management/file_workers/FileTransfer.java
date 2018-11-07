package com.multithreads.files_manager.management.file_workers;

import com.multithreads.files_manager.statistics.TaskReport;
import com.multithreads.files_manager.statistics.TaskTracker;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Transfers a certain number of bytes from one file to another.
 */
public class FileTransfer extends Thread {

    /**
     * Root logger.
     */
    private static final Logger logger = Logger.getLogger("transfer-logs");

    /**
     * File from which to transfer the bytes.
     */
    private File fromFile;

    /**
     * Pointer offset in the file from which to transfer the bytes.
     */
    private long fromFileOffset;

    /**
     * Number of bytes to transfer.
     */
    private long length;

    /**
     * FileTransfer destination file.
     */
    private File toFile;

    /**
     * Pointer offset in the transfer destination file.
     */
    private long toFileOffset;

    /**
     * Tool for interaction with the src.main.com.multithreads.files_manager.statistics module.
     */
    private TaskTracker taskTracker = new TaskTracker();


    /**
     * Initializes class fields.
     *
     * @param fromFile       file from which to transfer the bytes
     * @param fromFileOffset pointer offset in the file from which to transfer the bytes
     * @param length         number of bytes to transfer
     * @param toFile         transfer destination file
     * @param toFileOffset   pointer offset in the transfer destination file
     * @param taskTracker    tool for interaction with the src.main.com.multithreads.files_manager.statistics module
     */
    public FileTransfer(File fromFile, long fromFileOffset, long length, File toFile, long toFileOffset) {
        this.fromFile = fromFile;
        this.fromFileOffset = fromFileOffset;
        this.length = length;
        this.toFile = toFile;
        this.toFileOffset = toFileOffset;
    }

    /**
     * Executes transfer.
     */
    @Override
    public void run() {
        final String threadName = Thread.currentThread().getName();
        logger.trace("FileTransfer started." + this);
        try {
            RandomAccessFile fromFile = new RandomAccessFile(this.fromFile, "r");
            RandomAccessFile toFile = new RandomAccessFile(this.toFile, "rw");
            fromFile.seek(fromFileOffset);
            toFile.seek(toFileOffset);

            final int bufferSize = FileCreator.BUFFER_SIZE;
            long alreadyRead = 0;
            while (fromFile.getFilePointer() - fromFileOffset < length) {
                if (bufferSize >= length) {

                    logger.trace("bufferSize >= needToRead. FilePointer: " + fromFile.getFilePointer() + this);

                    long time = copyToFileAndGetTime(fromFile, toFile, length);
                    alreadyRead = alreadyRead + length;

                    trackTaskProcess(length, threadName, alreadyRead, time);

                } else {

                    logger.trace("bufferSize < needToRead. FilePointer: " + fromFile.getFilePointer() + this);

                    long time = copyToFileAndGetTime(fromFile, toFile, bufferSize);
                    length = length - bufferSize;
                    alreadyRead = alreadyRead + bufferSize;

                    trackTaskProcess(bufferSize, threadName, alreadyRead, time);
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
        logger.trace("FileTransfer completed." + this);
    }


    void trackTaskProcess(long bufferSize, String threadName, long alreadyRead, long time) {
        taskTracker.addCompletedTasks(bufferSize);
        taskTracker.addReportPerSection(threadName, new TaskReport(alreadyRead, length));
        taskTracker.setBufferTasks(bufferSize);
        taskTracker.setBufferTimeNanoSec(time);
    }

    /**
     * Reading and writing buffer bytes from one file to another.
     *
     * @param fromFile randomAccessFile
     * @param toFile   randomAccessFile
     * @param bufferSize           size of buffer
     * @return spent time in nanoseconds
     * @throws IOException if an I/O error occurs
     */

    private long copyToFileAndGetTime(RandomAccessFile fromFile, RandomAccessFile toFile, long bufferSize) throws IOException {
        byte[] buffer = new byte[(int) bufferSize];
        long startTime = System.nanoTime();
        logger.trace("StartTime: " + startTime + this);
        fromFile.read(buffer);
        toFile.write(buffer);
        long endTime = System.nanoTime();
        logger.trace("EndTime: " + endTime + this);
        return endTime - startTime;
    }

    @Override
    public String toString() {
        return "FileTransfer{" +
                "threadName='" + Thread.currentThread().getName() + '\'' +
                ", fromFile=" + fromFile +
                ", fromFileOffset=" + fromFileOffset +
                ", length=" + length +
                ", toFile=" + toFile +
                ", toFileOffset=" + toFileOffset +
                '}';
    }
}
