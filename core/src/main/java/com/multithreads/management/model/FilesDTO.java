package com.multithreads.management.model;

import java.io.File;

public class FilesDTO {

    /**
     * File from which to transfer the bytes.
     */
    private File fileToRead;

    /**
     * FileFillTask destination file.
     */
    private File fileToWrite;

    /**
     * Pointer offset in the file from which to transfer the bytes.
     */
    private long fileToReadOffset;

    private long fileToWriteOffset;

    /**
     * Number of bytes to transfer.
     */
    private long fileWriteLength;

    /**
     * Initializes class fields.
     *
     * @param fileToRead       file from which to transfer the bytes
     * @param fileToReadOffset pointer offset in the file from which to transfer the bytes
     * @param fileWriteLength         number of bytes to transfer
     * @param fileToWrite         transfer destination file
     * @param fileToWriteOffset   pointer offset in the transfer destination file
     */
   public FilesDTO(File fileToRead, File fileToWrite, long fileToReadOffset, long fileToWriteOffset, long fileWriteLength){

           this.fileToRead = fileToRead;
           this.fileToWrite = fileToWrite;
           this.fileWriteLength = fileWriteLength;
           this.fileToReadOffset = fileToReadOffset;
           this.fileToWriteOffset = fileToWriteOffset;
    }

    public File getFileToRead() {
        return fileToRead;
    }

    public File getFileToWrite() {
        return fileToWrite;
    }


    public long getFileToReadOffset() {
        return fileToReadOffset;
    }

    public long getFileToWriteOffset() {
        return fileToWriteOffset;
    }

    public long getFileWriteLength() {
        return fileWriteLength;
    }

}
