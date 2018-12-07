package com.multithreads.management.model;

import java.io.File;

/**
 * Files data transfer object to transfer file data into task
 */
public class FilesCurator {

    /**
     * File from which to transfer the bytes.
     */
    private File fileRead;

    /**
     * FileCopyist destination file.
     */
    private File fileWrite;

    /**
     * Pointer offset in the file from which to transfer the bytes.
     */
    private long readOffset;

    /**
     * Pointer offset in the file to write transfer the bytes.
     */
    private long writeOffset;

    /**
     * Number of bytes to transfer.
     */
    private long writeLength;

    /**
     * Initializes class fields.
     *
     * @param fileRead    file from which to transfer the bytes
     * @param readOffset  pointer offset in the file from which to transfer the bytes
     * @param writeLength   number of bytes to transfer
     * @param fileWrite   transfer destination file
     * @param writeOffset pointer offset in the transfer destination file
     */
    public FilesCurator(File fileRead, File fileWrite, long readOffset, long writeOffset, long writeLength) {

        this.fileRead = fileRead;
        this.fileWrite = fileWrite;
        this.writeLength = writeLength;
        this.readOffset = readOffset;
        this.writeOffset = writeOffset;
    }

    public File getFileRead() {
        return fileRead;
    }

    public File getFileWrite() {
        return fileWrite;
    }

    public long getReadOffset() {
        return readOffset;
    }

    public long getWriteOffset() {
        return writeOffset;
    }

    public long getWriteLength() {
        return writeLength;
    }

    @Override
    public String toString(){
        return new StringBuilder().append("FilesCurator { File fileRead(file to read data from): ").append(fileRead.getAbsolutePath())
                .append("; File fileWrite(file to write data into): ").append(fileWrite.getAbsolutePath())
                .append("; long readOffset = ").append(readOffset).append("; long writeOffset = ").append(writeOffset)
                .append("; long writeLength = ").append(writeLength).append(" }").toString();
    }
}

