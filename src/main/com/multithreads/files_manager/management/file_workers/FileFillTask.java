package com.multithreads.files_manager.management.file_workers;

import com.multithreads.files_manager.management.model.FilesDTO;
import com.multithreads.files_manager.statistics.StatisticService;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.Callable;

/**
 * Transfers a certain number of bytes from one file to another.
 */
public class FileFillTask implements Callable {

    private final StatisticService statisticService;

    private final FilesDTO filesDTO;


    public FileFillTask(FilesDTO filesDTO, StatisticService statisticService) {
        this.filesDTO = filesDTO;
        this.statisticService = statisticService;
    }

    /**
     * Executes transfer.
     */
    @Override
    public File call() {
        final String threadName = Thread.currentThread().getName();
        statisticService.getLogger().trace("FileFillTask started." + this);
        try {
            RandomAccessFile fileToRead = new RandomAccessFile(filesDTO.getFileToRead(), "r");
            RandomAccessFile fileToWrite = new RandomAccessFile(filesDTO.getFileToWrite(), "rw");
            fileToRead.seek(filesDTO.getFileToReadOffset());
            fileToWrite.seek(filesDTO.getFileToWriteOffset());
            
            final int bufferSize = FileCreator.BUFFER_SIZE;
            long alreadyRead = 0;
            long fileLength = filesDTO.getFileLength();
            while (fileToRead.getFilePointer() - filesDTO.getFileToReadOffset() < fileLength) {
                if (bufferSize >= fileLength) {

                    //statisticService.getLogger().trace("bufferSize >= needToRead. FilePointer: " + fromFile.getFilePointer() + this);
                    long time = copyFileAndGetSpentTime(fileToRead, fileToWrite, fileLength);
                    alreadyRead += fileLength;
                    statisticService.trackTaskProcess(fileLength, threadName, alreadyRead, time);

                } else {
                   // statisticService.getLogger().trace("bufferSize < needToRead. FilePointer: " + fromFile.getFilePointer() + this);
                    long time = copyFileAndGetSpentTime(fileToRead, fileToWrite, bufferSize);
                    fileLength -= bufferSize;
                    alreadyRead += bufferSize;
                    statisticService.trackTaskProcess(bufferSize, threadName, alreadyRead, time);
                }
            }
            return filesDTO.getFileToWrite();
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
        finally {
            statisticService.getLogger().trace("FileFillTask completed." + this);
        }
    }

    /**
     * Reading and writing buffer bytes from one file to another.
     *
     * @param fileToRead randomAccessFile
     * @param fileToWrite   randomAccessFile
     * @param bufferSize           size of buffer
     * @return spent time in nanoseconds
     * @throws IOException if an I/O error occurs
     */
    private long copyFileAndGetSpentTime(RandomAccessFile fileToRead, RandomAccessFile fileToWrite, long bufferSize) throws IOException {
        byte[] buffer = new byte[(int) bufferSize];
        long startTime = System.nanoTime();
        statisticService.getLogger().trace("StartTime: " + startTime + this);
        fileToRead.read(buffer);
        fileToWrite.write(buffer);
        long endTime = System.nanoTime();
        statisticService.getLogger().trace("EndTime: " + endTime + this);
        return endTime - startTime;
    }
}
