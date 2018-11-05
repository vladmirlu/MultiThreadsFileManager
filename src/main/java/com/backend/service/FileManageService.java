package com.backend.service;

import com.backend.model.UploadedFile;


import com.backend.thread.SomeThread;
import com.backend.thread.WorkerThread;
import com.backend.validator.FileValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class FileManageService {

    @Autowired
    FileValidator fileValidator;

    public ModelAndView manageUploadedFile(UploadedFile uploadedFile, BindingResult result){

        MultipartFile file = uploadedFile.getFile();
        fileValidator.validate(uploadedFile, result);

        String fileName = file.getOriginalFilename();

        if (result.hasErrors()) {
            return new ModelAndView("uploadForm");
        }
        threadsMain(file, fileName);

        return new ModelAndView("showFile", "message", fileName);
    }


    private void threadsMain(MultipartFile file, String fileName){
        try {
            PipedOutputStream po = new PipedOutputStream();
            PipedInputStream pi = new PipedInputStream(po);

            ExecutorService service = Executors.newCachedThreadPool();
            for (int i = 0; i < 7; i++) {

                service.submit(() -> {
                    try {
                    SomeThread provider = new SomeThread(Thread.currentThread().getName(), file, fileName, pi, po);
                    provider.goSuspend();
                    provider.start();
                    Thread.sleep(100);
                    WorkerThread worker = new WorkerThread("Worker", pi, po);
                    worker.start();
                    Thread.sleep(200);
                    provider.goResume();
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
