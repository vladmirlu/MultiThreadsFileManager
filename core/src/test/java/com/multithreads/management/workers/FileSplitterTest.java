package com.multithreads.management.workers;

import org.junit.Before;
import org.junit.Test;

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

import static org.junit.Assert.*;

public class FileSplitterTest {

    private String filePath;

    private String partFileSize;

    private String resourcesPath = "src/main/resources/test.properties";

    @Before
    public void setUp() throws IOException {

        partFileSize = "5";
        resourcesPath = "src/main/resources/test.properties";
        FileInputStream fis = new FileInputStream(resourcesPath);
        ResourceBundle bundle = new PropertyResourceBundle(fis);

        File directory = new File(bundle.getString("testDir"));
        if(!directory.exists()) Files.createDirectory(Paths.get(directory.getPath()));

        filePath = bundle.getString("originalFile");
        File file = new File(filePath);
        if(file.exists()) Files.delete(Paths.get(file.getPath()));

        RandomAccessFile raFile = new RandomAccessFile(file, "rw");
        raFile.writeChars("This is the test of splitting");

        File splitDir = new File(bundle.getString("splitFilesDirectory"));
        if(splitDir.exists()){
            if(splitDir.listFiles() != null){
                for(File f: splitDir.listFiles()){
                    Files.delete(f.toPath());
                }
            }
            Files.delete(Paths.get(splitDir.getPath()));
        }
    }

    @Test
    public void splitTest() throws IOException{

        FileService fileService = new FileService(resourcesPath);
        FileSplitter splitter = new FileSplitter();
        List<Future<File>> futures = splitter.split(filePath, partFileSize, fileService);
        assertEquals(12, futures.size());
    }
}

