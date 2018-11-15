package com.multithreads.management.workers;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * Merge constants.
 */
public class FileMerger {

    /**
     * Merges files.
     *
     * @return list with merged file
     * @throws IOException             if an I/O error occurs
     */
    public List <File> merge(String directoryPath, FileService fileService) throws IOException{

        List<File> files = fileService.getSplitFilesList(directoryPath);
        File originalFile = fileService.createOriginalFile(files);
        files.sort(Comparator.comparingInt(o -> Integer.parseInt(FilenameUtils.getBaseName(o.getName()))));

        long count = files.get(files.size() - 1).length() < files.get(0).length() ? files.size() - 1 : files.size();
        List<Future<File>> futures = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            long num = Integer.parseInt(FilenameUtils.getBaseName(files.get(i).getName()));
            Future<File> future = fileService.getWorkerFuture(files.get(i), files.get(i).length(), 0, num * files.get(i).length(), originalFile);
            futures.add(future);
        }
        if (count == files.size() - 1) {
            long totalSize = fileService.getFileProvider().calculateTotalSize(files);
            Future<File> future = fileService.getWorkerFuture(files.get(files.size() - 1), files.get(files.size() - 1).length(), 0, totalSize - files.get(files.size() - 1).length(), originalFile);
            futures.add(future);
        }
        return futures.stream().map(future -> { try { return future.get(10, TimeUnit.SECONDS); } catch (InterruptedException | ExecutionException |
                TimeoutException e) { throw new RuntimeException(e); } }).collect(Collectors.toList());
    }
}
