package com.multithreads.files_manager.management.file_workers;

import com.multithreads.files_manager.statistics.StatisticService;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Merge constants.
 */
public class FileMerger {

    private final FileService fileService;

    private final StatisticService statisticService;

    public FileMerger( FileService fileService, StatisticService statisticService) {
        this.fileService = fileService;
        this.statisticService = statisticService;
    }

    /**
     * Merges files.
     *
     * @return list with merged file
     * @throws ExecutionException      if the computation threw an exception
     * @throws InterruptedException    in case of thread interrupting
     * @throws IOException             if an I/O error occurs
     */
    public List<File> merge() throws IOException, ExecutionException, InterruptedException {

        List<File> files = fileService.parseFiles();
        File originalFile = fileService.getOriginalFile(files);
        files.sort(Comparator.comparingInt(o -> Integer.parseInt(FilenameUtils.getBaseName(o.getName()))));

        long iterations = files.get(files.size() - 1).length() < files.get(0).length() ? files.size() - 1 : files.size();
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < iterations; i++) {
            long num = Integer.parseInt(FilenameUtils.getBaseName(files.get(i).getName()));
            Future<?> f = fileService.getWorkerFuture(files.get(i), files.get(i).length(), 0, num * files.get(i).length(), originalFile, statisticService);
            futures.add(f);
        }
        if (iterations == files.size() - 1) {
            long totalSize = fileService.getFileCreator().calculateTotalSize(files);
            Future<?> f = fileService.getWorkerFuture(files.get(files.size() - 1), files.get(files.size() - 1).length(), 0, totalSize - files.get(files.size() - 1).length(), originalFile, statisticService);
            futures.add(f);
        }
        List<File> originalFiles = new ArrayList<>();
        originalFiles.add(originalFile);

        fileService.getFileWorkersPool().shutdown();
        statisticService.setStatistic(futures);

        return originalFiles;
    }
}
