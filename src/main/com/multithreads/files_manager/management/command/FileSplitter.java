package com.multithreads.files_manager.management.command;

import com.multithreads.files_manager.management.FileService;
import com.multithreads.files_manager.management.exception.InvalidCommandException;
import com.multithreads.files_manager.management.splitter.SizeUnits;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Split command.
 */
public class FileSplitter {

    private final FileService fileService;


    public FileSplitter(FileService fileService) {
       this.fileService = fileService;
    }

    /**
     * Splits file.
     *
     * @return list of files
     * @throws ExecutionException      if the computation threw an exception
     * @throws InterruptedException    in case of thread interrupting
     * @throws InvalidCommandException in case of command invalidity
     * @throws IOException             if an I/O error occurs
     */

    public List<File> execute(String splitSize) throws IOException, ExecutionException, InterruptedException {

        File file = new File("/home/user7/Documents/temp/temp.txt");

        long partSize = parseSize(splitSize);
        long fileSize = file.length();
        long numSplits = fileSize / partSize;
        long remainingBytes = fileSize % partSize;

        List<Future<?>> futures = new ArrayList<>();
        List<File> files = new ArrayList<>();

        Files.createDirectory(Paths.get(file.getParent() + "/parts"));
        for (long i = 0; i < numSplits; i++) {
            File partFile = new File(file.getParent() + "/parts/" + i + "." + FilenameUtils.getExtension(file.getName()));
            Future<?> f = fileService.getWorkerFuture(file, partSize,i * partSize, 0, partFile);
            futures.add(f);
            files.add(partFile);
        }
        if (remainingBytes > 0) {

            File partFile = new File(file.getParent() + "/parts/" + (numSplits) + "." + FilenameUtils.getExtension(file.getName()));
            Future<?> f = fileService.getWorkerFuture(file, remainingBytes,fileSize - remainingBytes,0, partFile);
            futures.add(f);
            files.add(partFile);
        }

        fileService.setStatistic(futures);
        return files;
    }

    public long parseSize(String sizeStr) {

        long size = Long.parseLong(sizeStr);
        for (SizeUnits sizeUnit : SizeUnits.values()) {
            if (sizeStr.endsWith(String.valueOf(sizeUnit))) {
                size = Long.parseLong(sizeStr.substring(0, sizeStr.indexOf(String.valueOf(sizeUnit))))
                        * sizeUnit.getCoefficient();
            }
        }

        return size;
    }
}
