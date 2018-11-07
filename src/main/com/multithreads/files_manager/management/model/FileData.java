package com.multithreads.files_manager.management.model;

import java.io.File;

public class FileData {

    /**
     * File from which to transfer the bytes.
     */
    private File fromFile;

    /**
     * FileTransfer destination file.
     */
    private File toFile;

    /**
     * Pointer offset in the file from which to transfer the bytes.
     */
    private long fromFileOffset;

    /**
     * Pointer offset in the transfer destination file.
     */
    private long toFileOffset;

    /**
     * Number of bytes to transfer.
     */
    private long fileLength;

    /**
     * Initializes class fields.
     * @param fromFile       file from which to transfer the bytes
     * @param fromFileOffset pointer offset in the file from which to transfer the bytes
     * @param fileLength         number of bytes to transfer
     * @param toFile         transfer destination file
     * @param toFileOffset   pointer offset in the transfer destination file
     */
   public FileData(File fromFile, File toFile, long fromFileOffset, long toFileOffset, long fileLength){
        this.fromFile = fromFile;
        this.toFile = toFile;
        this.fromFileOffset = fromFileOffset;
        this.toFileOffset = toFileOffset;
        this.fileLength = fileLength;
    }

    public File getFromFile() {
        return fromFile;
    }

    public File getToFile() {
        return toFile;
    }

    public long getFromFileOffset() {
        return fromFileOffset;
    }

    public long getToFileOffset() {
        return toFileOffset;
    }

    public long getFileLength() {
        return fileLength;
    }
}
