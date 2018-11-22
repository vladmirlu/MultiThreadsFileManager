package com.multithreads.management.workers;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.File;
import java.util.Collections;

public class FileServiceTest {

   private FileService fileService;

    @Before
    public void setUp() {
        String resourcesPath = "src/main/resources/test.properties";
        fileService = new FileService(resourcesPath);
    }

    @Test
    public void createOriginalFileCloneTest(){
        File file = fileService.createOriginalFileClone(Collections.singletonList(new File("test")));
        assertEquals(file.getName(),"original.");
    }

    @Test
    public void calculateTotalSizeTest(){
        long size = fileService.calculateTotalSize(Collections.singletonList(new File("test")));
        assertEquals(0, size);
    }
}