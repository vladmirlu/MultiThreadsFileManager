package com.multithreads.management.workers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FileMergerTest {

    @Mock
    FileService fileService;

    private String directoryPath = "testDir";

    private File file1, file2, mergedFile;

    List<File> files;
    @Mock
    Future<File> future;

    @InjectMocks
    FileMerger fileMerger;

    @Before
    public void setUp() throws IOException {
        file1 = new File("1.test");
        file2 = new File("2.test");
        RandomAccessFile raFile = new RandomAccessFile(file1, "rw");
        raFile.writeChars("test");
        raFile = new RandomAccessFile(file2, "rw");
        raFile.write(1);
        mergedFile = new File("mergedFile");

        files = Arrays.asList(file1, file2);
    when(fileService.findSplitFilesList(directoryPath)).thenReturn(files);
    when(fileService.createOriginalFileClone(files)).thenReturn(mergedFile);
    when(fileService.createTaskFuture(any(File.class), anyLong(), anyLong(), anyLong(), any(File.class))).thenReturn(future);
    }

    @Test
    public void mergeTest1() throws IOException{
        List<Future<File>> futures = fileMerger.merge(directoryPath, fileService);
        verify(fileService).findSplitFilesList(directoryPath);
        verify(fileService).createOriginalFileClone(files);
        verify(fileService).createTaskFuture(file1, 8, 0, 8, mergedFile);
        verify(fileService).calculateTotalSize(files);
        assertEquals(future, futures.get(0));
    }
}
