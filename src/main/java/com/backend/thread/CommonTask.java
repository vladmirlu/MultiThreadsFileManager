/*
package com.backend.thread;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class CommonTask implements Runnable {
    private MultipartFile file;
    private String fileName;
    private PipedInputStream pi;
    private PipedOutputStream po;
    private Thread tread;
    private boolean suspendFlag;

    public CommonTask(String name, MultipartFile file, String fileName, PipedInputStream pi, PipedOutputStream po) {
        this.file = file;
        this.fileName = fileName;
        this.pi = pi;
        this.po = po;

        System.out.println("Create tread: " + this.tread.getName());
        this.tread = new Thread(this);
        this.tread.setName(name);
        this.tread.start();

    }

    @Override
    public void run() {

        System.out.println("Thread " + this.tread.getName() + " started");
        try {

            if(this.tread.getName().equals("Worker")){
                System.out.println("Thread Worker started");

                StringBuilder builder = new StringBuilder();
                byte[] data = new byte[1024];
                int len = pi.read(data, 0, 1024);
                for (int i = 0; i < len; i++) {
                    builder.append(data[i]);
                }
//            System.out.println(builder.toString());
                String hash = getMD5hash(builder.toString());
                po.write(hash.getBytes());
                // pi.close();
                System.out.println("Worker Thread generates the MD5 hash: " + hash);

                Thread.sleep(100);
                synchronized (this) {
                    suspendFlag = false;
                    notify();
                }
            }

            else {
                InputStream inputStream = file.getInputStream();

                File newFile = new File("C:/Users/User/Documents/" + this.tread.getName() + fileName);
                if (!newFile.exists()) {
                    newFile.createNewFile();
                }
                OutputStream outputStream = new FileOutputStream(newFile);

                int read = 0;
                byte[] bytes = new byte[1024];
                while ((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                    po.write(bytes, 0, read);
                }
                //po.close();


                Thread.sleep(100);
                synchronized (this) {
                    while (suspendFlag) {
                        this.wait();
                    }
                }

                byte[] data = new byte[1024];
                int len = pi.read(data, 0, 1024);
                for (int i = 0; i < len; i++) {
                    outputStream.write(data[i]);
                }
                //pi.close();
                System.out.println("Provider " + this.tread.getName() + " writes the hash to the file");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getMD5hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);
            // Now we need to zero pad it if you actually want the full 32 chars.
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

   public void goSuspend() {
        suspendFlag = true;
    }
    public synchronized void goResume() {
        suspendFlag = false;
        notify();
    }
}
*/
