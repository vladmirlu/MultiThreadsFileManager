package com.multithreads.files_manager.management.file_workers;

import com.multithreads.files_manager.statistics.StatisticService;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Split constants.
 */
public class FileSplitter {

    private final FileService fileService;
    private final StatisticService statisticService;


    public FileSplitter(FileService fileService, StatisticService statisticService) {
       this.fileService = fileService;
       this.statisticService = statisticService;
    }
    /**
     * Splits file.
     *
     * @return list of files
     * @throws ExecutionException      if the computation threw an exception
     * @throws InterruptedException    in case of thread interrupting
     * @throws IOException             if an I/O error occurs
     */

    public List<File> split(String filePath, String splitFileSize) throws IOException, ExecutionException, InterruptedException {

        File file = fileService.getFileCreator().getFile(filePath);

        long partSize = fileService.parseSize(filePath, splitFileSize);
        long fileSize = file.length();
        long splitsQuantity = fileSize / partSize;
        List<Future<File>> futures = new ArrayList<>();

        Files.createDirectory(Paths.get(file.getParent() + "/parts"));
        for (long i = 0; i < splitsQuantity; i++) {
            File partFile = new File(file.getParent() + "/parts/" + i + "." + FilenameUtils.getExtension(file.getName()));
            Future<File> f = fileService.getWorkerFuture(file, partSize,i * partSize, 0, partFile, statisticService);
            futures.add(f);
        }

        long bytesLeftAmount = fileSize % partSize;
        if (bytesLeftAmount > 0) {
            File partFile = new File(file.getParent() + "/parts/" + (splitsQuantity) + "." + FilenameUtils.getExtension(file.getName()));
            Future<File> f = fileService.getWorkerFuture(file, bytesLeftAmount,fileSize - bytesLeftAmount,0, partFile, statisticService);
            futures.add(f);
        }
       // statisticService.setStatistic(futures);
        return  futures.stream().map(future -> { try { return future.get(); } catch (InterruptedException | ExecutionException e) { throw new RuntimeException(e); } }).collect(Collectors.toList());
    }
}
