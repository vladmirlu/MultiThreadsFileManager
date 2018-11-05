package com.backend.thread;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;


public class SomeThread extends Thread {
    private MultipartFile file;
    private String fileName;
    private PipedInputStream pi;
    private PipedOutputStream po;

    private boolean suspendFlag;

    public SomeThread(String name, MultipartFile file, String fileName, PipedInputStream pi, PipedOutputStream po) {
        this.setName(name);
        this.file = file;
        this.fileName = fileName;
        this.pi = pi;
        this.po = po;
        System.out.println("Create tread: " + getName());
    }

    @Override
    public void run() {
        System.out.println("Thread " + getName() + " started");
        try {
            InputStream inputStream = file.getInputStream();

            File newFile = new File("C:/Users/User/Documents/" + getName() + fileName);
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
                System.out.println("Provider " + getName() + " writes the hash to the file");

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
