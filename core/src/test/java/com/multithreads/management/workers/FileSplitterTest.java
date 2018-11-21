package com.multithreads.management.workers;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.concurrent.Future;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class FileSplitterTest {

    @Mock
    FileService fileServiceMock;

    private String filePath;

    private String partFileSize;

    private String resourcesPath = "src/main/resources/test.properties";

    private File file;

    @Mock
    Future<File> future;

    @InjectMocks
    FileSplitter fileSplitter;

   private ResourceBundle bundle;

    @Before
    public void setUp() throws IOException {
        partFileSize = "10";
        resourcesPath = "src/main/resources/test.properties";
        FileInputStream fis = new FileInputStream(resourcesPath);
        bundle = new PropertyResourceBundle(fis);

        File directory = new File(bundle.getString("testDir"));
        if(!directory.exists()) Files.createDirectory(Paths.get(directory.getPath()));

        filePath = bundle.getString("originalFile");
        file = new File(filePath);
        if(file.exists()) Files.delete(Paths.get(file.getPath()));

        RandomAccessFile raFile = new RandomAccessFile(file, "rw");
        raFile.writeChars("This is the test of splitting");

        File splitDir = new File(bundle.getString("splitFilesDirectory"));
        if(splitDir.exists()){
            if(splitDir.listFiles() != null){
                for(File file: splitDir.listFiles()){
                    Files.delete(file.toPath());
                }
            }
            Files.delete(Paths.get(splitDir.getPath()));
        }
    }

    @Test
    public void splitUnitTest() throws IOException {
        when(fileServiceMock.createTaskFuture(any(File.class), anyLong(), anyLong(), anyLong(), any(File.class))).thenReturn(future);
        when(fileServiceMock.findExistingFile(filePath)).thenReturn(file);
        File splitFile = new File(bundle.getString("splitFile1"));
        List<Future<File>> futures = fileSplitter.split(filePath, partFileSize, fileServiceMock);
        verify(fileServiceMock).findExistingFile(filePath);
        verify(fileServiceMock).createTaskFuture(file, 10, 0, 0, splitFile);
        assertEquals(future, futures.get(0));
    }

    @Test
    public void splitTest() throws IOException{
        partFileSize = "5";
        FileService fileService = new FileService(resourcesPath);
        FileSplitter splitter = new FileSplitter();
        List<Future<File>> futures = splitter.split(filePath, partFileSize, fileService);
        assertEquals(12, futures.size());
    }
}

