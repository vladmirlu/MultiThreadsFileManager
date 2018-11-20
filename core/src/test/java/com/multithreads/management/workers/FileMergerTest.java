package com.multithreads.management.workers;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.concurrent.Future;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class FileMergerTest {

    @Mock
    FileService fileServiceMock;

    private File originalFile, clone, split1, split2;

    @Mock
    Future<File> future;

   private ResourceBundle bundle;

   private String resourcesPath = "src/main/resources/test.properties";

    @Spy
    @InjectMocks
    FileMerger fileMergerSpy;

    @Before
    public void setUp() throws IOException {

        FileInputStream fis = new FileInputStream(resourcesPath);
        bundle = new PropertyResourceBundle(fis);

        File splitDir = new File(bundle.getString("splitFilesDirectory"));
        if(splitDir.exists()){
            if(splitDir.listFiles() != null){
                for(File file: splitDir.listFiles()){
                    Files.delete(file.toPath());
                }
            }
          Files.delete(Paths.get(splitDir.getPath()));
        }

        File testDir = new File(bundle.getString("testDir"));
        if(!testDir.exists()) Files.createDirectory(Paths.get(testDir.getPath()));
        originalFile = new File(bundle.getString("originalFile"));

         Files.createDirectory(Paths.get(splitDir.getPath()));
        split1 = new File(splitDir.getPath() + "/0.txt");
        split2 = new File(splitDir.getPath() +"/1.txt");

        RandomAccessFile raFile = new RandomAccessFile(originalFile, "rw");
        raFile.writeChars("This is the test of merging");
        raFile = new RandomAccessFile(split1, "rw");
        raFile.writeChars("This is the test");
        raFile = new RandomAccessFile(split2, "rw");
        raFile.writeChars(" of merging");

        clone = new File(splitDir.getPath() + "/original.txt");
        if(clone.exists()){
            Files.delete(Paths.get(clone.getPath()));
        }
    }

    @Test
    public void mergeUnitTest() throws IOException{
        File anyFile = new File("mergedFile");
        String directoryPath = "somewhat";
        List<File> files = Arrays.asList(split1, split2);
        when(fileServiceMock.findSplitFilesList(directoryPath)).thenReturn(files);
        when(fileServiceMock.createOriginalFileClone(files)).thenReturn(anyFile);
        when(fileServiceMock.createTaskFuture(any(File.class), anyLong(), anyLong(), anyLong(), any(File.class))).thenReturn(future);
        List<Future<File>> futures = fileMergerSpy.merge(directoryPath, fileServiceMock);
        verify(fileServiceMock).findSplitFilesList(directoryPath);
        verify(fileServiceMock).createOriginalFileClone(files);
        verify(fileServiceMock).createTaskFuture(split1, 32, 0, 0, anyFile);
        verify(fileServiceMock).calculateTotalSize(files);
        assertEquals(future, futures.get(0));
    }

    @Test
    public void mergeIntegrationTest() throws NoSuchAlgorithmException, IOException{
       FileService fileService = new FileService(Logger.getRootLogger(), resourcesPath);
       FileMerger fileMerger = new FileMerger();
       List<Future<File>> futures = fileMerger.merge(bundle.getString("splitFilesDirectory"), fileService);
       assertEquals(2, futures.size());
       assertTrue(filesMatchVerifying(originalFile, clone));
    }


     boolean filesMatchVerifying(File file1, File file2)
            throws NoSuchAlgorithmException, IOException {

        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(Files.readAllBytes(Paths.get(file1.getPath())));
        byte[] file1Digest = md.digest();
        String file1Checksum = DatatypeConverter
                .printHexBinary(file1Digest).toUpperCase();

         md.update(Files.readAllBytes(Paths.get(file2.getPath())));
         byte[] file2Digest = md.digest();
         String file2Checksum = DatatypeConverter
                 .printHexBinary(file2Digest).toUpperCase();

        return file1Checksum.equals(file2Checksum);
    }
}

