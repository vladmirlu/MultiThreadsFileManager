package com.backend.thread;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class WorkerThread extends Thread {

    private PipedInputStream pi;
    private PipedOutputStream po;

    private boolean suspendFlag;

    public WorkerThread(String name, PipedInputStream pi, PipedOutputStream po) {
        this.setName(name);
        this.pi = pi;
        this.po = po;
        System.out.println("Create tread: " + name);
    }

    public void run() {

             try {

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

                    /* Thread.sleep(100);
                     synchronized (this){
                         notify();
                     }
             } catch (InterruptedException e) {
                 e.printStackTrace();*/

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
