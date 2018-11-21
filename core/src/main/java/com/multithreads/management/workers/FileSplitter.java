package com.multithreads.management.workers;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import java.util.List;
import java.util.concurrent.Future;

/**
 * Executor to split files into one original file.
 */
public class FileSplitter {

    /**
     * Root logger.
     */
    private final Logger logger = Logger.getLogger(FileSplitter.class);

    /**
     * Splits file into parts.
     *
     * @param filePath    the file to split path
     * @param fileService service to make multi threads file merging
     * @return list Future<File> file splitting tasks
     * @throws IOException when the IOException occurs during file work
     */
    public List<Future<File>> split(String filePath, String partFileSize, FileService fileService) throws IOException {

        File file = fileService.findExistingFile(filePath);
        logger.debug("Find original file '" + file.getName() + "' to split it into parts. The part file size = " + partFileSize);
        long splitFileLength = file.length() <= Long.parseLong(partFileSize) ? file.length() : Long.parseLong(partFileSize);
        long bytesLeftAmount = file.length() % splitFileLength;
        long partsQuantity = (file.length() - bytesLeftAmount) / splitFileLength;
        List<Future<File>> futures = new ArrayList<>();

        Files.createDirectory(Paths.get(file.getParent() + "/split"));
        for (long i = 0; i < partsQuantity; i++) {
            File partFile = new File(file.getParent() + "/split/" + i + "." + FilenameUtils.getExtension(file.getName()));
            Future<File> f = fileService.createTaskFuture(file, splitFileLength, i * splitFileLength, 0, partFile);
            futures.add(f);
            logger.debug("Creating Future<File> to write data from '" + file.getName() + "' into split part file '" + partFile.getName() + "'");
        }
        if (bytesLeftAmount > 0) {
            File partFile = new File(file.getParent() + "/split/" + partsQuantity + "." + FilenameUtils.getExtension(file.getName()));
            Future<File> f = fileService.createTaskFuture(file, bytesLeftAmount, file.length() - bytesLeftAmount, 0, partFile);
            futures.add(f);
            logger.debug("Creating Future<File> to write data '" + file.getName() + "' into split part file '" + partFile.getName() + "'");
        }
        fileService.turnOnStatisticPrint();
        return futures;
    }
}
