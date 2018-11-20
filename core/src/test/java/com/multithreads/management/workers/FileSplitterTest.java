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
import java.util.List;
import java.util.concurrent.Future;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class FileSplitterTest {

    @Mock
    FileService fileService;

    private String filePath = "filePath";

    private String partFileSize = "2";

    private File file;

    @Mock
    Future<File> future;

    @InjectMocks
    FileSplitter fileSplitter;

    @Before
    public void setUp() throws IOException {
        file = new File("C:/Users/User/IdeaProjects/Multi_threads_file_manager/core/test.txt");
        if(!file.exists()) file.createNewFile();
        RandomAccessFile raFile = new RandomAccessFile(file, "rw");
        raFile.writeChars("This is the test");

        when(fileService.findExistingFile(filePath)).thenReturn(file);
        when(fileService.createTaskFuture(any(File.class), anyLong(), anyLong(), anyLong(), any(File.class))).thenReturn(future);
    }

    @Test
    public void splitTest() throws IOException {
        File splitFile = new File("C:/Users/User/IdeaProjects/Multi_threads_file_manager/core/split/0.txt");
        List<Future<File>> futures = fileSplitter.split(filePath, partFileSize, fileService);
        verify(fileService).findExistingFile(filePath);
        verify(fileService).createTaskFuture(file, 2, 0, 0, splitFile);
        assertEquals(future, futures.get(0));
    }
}

