package com.multithreads.files_manager.management.command;

import com.multithreads.files_manager.management.FileService;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Merge command.
 */
public class FileMerger {

    private final FileService fileService;


    public FileMerger( FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * Merges files.
     *
     * @param args command arguments
     * @return list with merged file
     * @throws ExecutionException      if the computation threw an exception
     * @throws InterruptedException    in case of thread interrupting
     * @throws IOException             if an I/O error occurs
     */
    public List<File> execute(final String[] args) throws IOException, ExecutionException, InterruptedException {

        List<File> files = fileService.parseFiles(args);
        File originalFile = fileService.getOriginalFile(files);
        files.sort(Comparator.comparingInt(o -> Integer.parseInt(FilenameUtils.getBaseName(o.getName()))));

        long iterations = files.get(files.size() - 1).length() < files.get(0).length() ? files.size() - 1 : files.size();
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < iterations; i++) {
            long num = Integer.parseInt(FilenameUtils.getBaseName(files.get(i).getName()));
            Future<?> f = fileService.getWorkerFuture(files.get(i), files.get(i).length(), 0, num * files.get(i).length(), originalFile);
            futures.add(f);
        }
        if (iterations == files.size() - 1) {
            long totalSize = fileService.getFileAssistant().calculateTotalSize(files);
            Future<?> f = fileService.getWorkerFuture(files.get(files.size() - 1), files.get(files.size() - 1).length(), 0, totalSize - files.get(files.size() - 1).length(), originalFile);
            futures.add(f);
        }
        List<File> originalFiles = new ArrayList<>();
        originalFiles.add(originalFile);
        fileService.setStatistic(futures);
        return originalFiles;
    }
}
