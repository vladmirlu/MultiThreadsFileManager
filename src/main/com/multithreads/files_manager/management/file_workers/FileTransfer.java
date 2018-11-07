package com.multithreads.files_manager.management.file_workers;

import com.multithreads.files_manager.management.model.FileData;
import com.multithreads.files_manager.statistics.StatisticService;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Transfers a certain number of bytes from one file to another.
 */
public class FileTransfer extends Thread {

    private final StatisticService statisticService;

    private final FileData fileData;


    public FileTransfer(FileData fileData, StatisticService statisticService) {
        this.fileData = fileData;
        this.statisticService = statisticService;
    }

    /**
     * Executes transfer.
     */
    @Override
    public void run() {
        final String threadName = Thread.currentThread().getName();
        statisticService.getLogger().trace("FileTransfer started." + this);
        try {
            RandomAccessFile fromFile = new RandomAccessFile(fileData.getFromFile(), "r");
            RandomAccessFile toFile = new RandomAccessFile(fileData.getToFile(), "rw");
            fromFile.seek(fileData.getFromFileOffset());
            toFile.seek(fileData.getToFileOffset());

            final int bufferSize = FileCreator.BUFFER_SIZE;
            long alreadyRead = 0;
            long fileLength = fileData.getFileLength();
            while (fromFile.getFilePointer() - fileData.getFromFileOffset() < fileLength) {
                if (bufferSize >= fileLength) {

                    statisticService.getLogger().trace("bufferSize >= needToRead. FilePointer: " + fromFile.getFilePointer() + this);
                    long time = copyToFileAndGetTime(fromFile, toFile, fileLength);
                    alreadyRead = alreadyRead + fileLength;
                    statisticService.trackTaskProcess(fileLength, threadName, alreadyRead, time);

                } else {

                    statisticService.getLogger().trace("bufferSize < needToRead. FilePointer: " + fromFile.getFilePointer() + this);
                    long time = copyToFileAndGetTime(fromFile, toFile, bufferSize);
                    fileLength = fileLength - bufferSize;
                    alreadyRead = alreadyRead + bufferSize;
                    statisticService.trackTaskProcess(bufferSize, threadName, alreadyRead, time);
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
        statisticService.getLogger().trace("FileTransfer completed." + this);
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
        statisticService.getLogger().trace("StartTime: " + startTime + this);
        fromFile.read(buffer);
        toFile.write(buffer);
        long endTime = System.nanoTime();
        statisticService.getLogger().trace("EndTime: " + endTime + this);
        return endTime - startTime;
    }
}
