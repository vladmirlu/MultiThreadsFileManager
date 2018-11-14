package com.multithreads.manager.management.workers;

import com.multithreads.manager.statistics.StatisticService;
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
    public List <Future<File>> split(String filePath, String partFileSize) throws IOException{

        File file = fileService.getFileCreator().getFile(filePath);
        statisticService.getTasksTracker().setTotalOfTask(file.length());

        long splitFileLength = file.length() <= Long.parseLong(partFileSize) ? file.length() : Long.parseLong(partFileSize);
        long bytesLeftAmount = file.length() % splitFileLength;
        long partsQuantity = (file.length() - bytesLeftAmount) / splitFileLength;
        List<Future<File>> futures = new ArrayList<>();

        Files.createDirectory(Paths.get(file.getParent() + "/split"));
        for (long i = 0; i < partsQuantity; i++) {
            File partFile = new File(file.getParent() + "/split/" + i + "." + FilenameUtils.getExtension(file.getName()));
            Future<File> f = fileService.getWorkerFuture(file, splitFileLength,i * splitFileLength, 0, partFile, statisticService);
            futures.add(f);
        }
        if (bytesLeftAmount > 0) {
            File partFile = new File(file.getParent() + "/split/" + partsQuantity + "." + FilenameUtils.getExtension(file.getName()));
            Future<File> f = fileService.getWorkerFuture(file, bytesLeftAmount,file.length() - bytesLeftAmount,0, partFile, statisticService);
            futures.add(f);
        }
        return  futures;
        /*futures.stream().map(future -> { try { return future.get(); } catch (InterruptedException | ExecutionException e) { throw new RuntimeException(e); } }).collect(Collectors.toList());*/
    }
}
