package com.multithreads.management.model;

import java.io.File;

/**
 * Files data transfer object to transfer file data into task
 */
public class FilesDTO {

    /**
     * File from which to transfer the bytes.
     */
    private File fileToRead;

    /**
     * FileCopyist destination file.
     */
    private File fileToWrite;

    /**
     * Pointer offset in the file from which to transfer the bytes.
     */
    private long toReadOffset;

    /**
     * Pointer offset in the file to write transfer the bytes.
     */
    private long toWriteOffset;

    /**
     * Number of bytes to transfer.
     */
    private long writeLength;

    /**
     * Initializes class fields.
     *
     * @param fileToRead    file from which to transfer the bytes
     * @param toReadOffset  pointer offset in the file from which to transfer the bytes
     * @param writeLength   number of bytes to transfer
     * @param fileToWrite   transfer destination file
     * @param toWriteOffset pointer offset in the transfer destination file
     */
    public FilesDTO(File fileToRead, File fileToWrite, long toReadOffset, long toWriteOffset, long writeLength) {

        this.fileToRead = fileToRead;
        this.fileToWrite = fileToWrite;
        this.writeLength = writeLength;
        this.toReadOffset = toReadOffset;
        this.toWriteOffset = toWriteOffset;
    }

    public File getFileToRead() {
        return fileToRead;
    }

    public File getFileToWrite() {
        return fileToWrite;
    }


    public long getToReadOffset() {
        return toReadOffset;
    }

    public long getToWriteOffset() {
        return toWriteOffset;
    }

    public long getWriteLength() {
        return writeLength;
    }

}
