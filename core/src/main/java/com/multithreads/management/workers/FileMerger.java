package com.multithreads.management.workers;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Executor to merge files into one original file.
 */
public class FileMerger {

    /**
     * FileMerger logger.
     */
    private final Logger logger;

    /**
     * create new file merger object and initialise logger
     */
    public FileMerger() {
        this.logger = Logger.getLogger(FileMerger.class);
    }

    /**
     * Collect split files, send them to Future to merge into one original file.
     *
     * @param directoryPath the directory path when are located split files
     * @param fileService   service to make multi threads file merging
     * @return list Future<File> file merging tasks
     * @throws IOException when the IOException occurs during file work
     */
    public List<Future<File>> merge(String directoryPath, FileService fileService) throws IOException {

        List<File> files = fileService.findSplitFilesList(directoryPath);
        File originalFile = fileService.createOriginalFileClone(files);
        logger.debug("Creating original file '" + originalFile.getName() + "' from split parts. Parts quantity = " + files.size());
        files.sort(Comparator.comparingInt(o -> Integer.parseInt(FilenameUtils.getBaseName(o.getName()))));

        long count = files.get(files.size() - 1).length() < files.get(0).length() ? files.size() - 1 : files.size();
        List<Future<File>> futures = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            long num = Integer.parseInt(FilenameUtils.getBaseName(files.get(i).getName()));
            Future<File> future = fileService.createTaskFuture(files.get(i), files.get(i).length(), 0, num * files.get(i).length(), originalFile);
            futures.add(future);
            logger.debug("Creating Future<File> to merge data from '" + files.get(i).getName() + "' to '" + originalFile.getName());
        }
        if (count == files.size() - 1) {
            long totalSize = fileService.calculateTotalSize(files);
            Future<File> future = fileService.createTaskFuture(files.get(files.size() - 1), files.get(files.size() - 1).length(), 0, totalSize - files.get(files.size() - 1).length(), originalFile);
            futures.add(future);
            logger.debug("Creating Future<File> to merge data from '" + files.get(files.size() - 1) + "' to '" + originalFile.getName());
        }
        return futures;
    }
}
