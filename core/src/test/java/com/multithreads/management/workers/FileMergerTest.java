package com.multithreads.management.workers;

import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.concurrent.Future;

import static org.junit.Assert.*;

public class FileMergerTest {

    private File originalFile, clone;

    private ResourceBundle bundle;

    private String resourcesPath = "src/main/resources/test.properties";


    @Before
    public void setUp() throws IOException {

        FileInputStream fis = new FileInputStream(resourcesPath);
        bundle = new PropertyResourceBundle(fis);

        File testDir = new File(bundle.getString("testDir"));
        if (!testDir.exists()) Files.createDirectory(Paths.get(testDir.getPath()));

        originalFile = new File(bundle.getString("originalFile"));
        if (originalFile.exists()) Files.delete(Paths.get(originalFile.getPath()));

        File splitDir = new File(bundle.getString("splitFilesDirectory"));
        if (splitDir.exists()) {
            if (splitDir.listFiles() != null) {
                for (File file : splitDir.listFiles()) {
                    Files.delete(file.toPath());
                }
            }
            Files.delete(Paths.get(splitDir.getPath()));
        }

        Files.createDirectory(Paths.get(splitDir.getPath()));
        File split1 = new File(splitDir.getPath() + "/0.txt");
        File split2 = new File(splitDir.getPath() + "/1.txt");

        RandomAccessFile raFile = new RandomAccessFile(originalFile, "rw");
        raFile.writeChars("This is the test of merging");
        raFile = new RandomAccessFile(split1, "rw");
        raFile.writeChars("This is the test");
        raFile = new RandomAccessFile(split2, "rw");
        raFile.writeChars(" of merging");

        clone = new File(splitDir.getPath() + "/original.txt");
        if (clone.exists()) {
            Files.delete(Paths.get(clone.getPath()));
        }
    }

    @Test
    public void mergeTest() throws NoSuchAlgorithmException, IOException {
        FileService fileService = new FileService(resourcesPath);
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

