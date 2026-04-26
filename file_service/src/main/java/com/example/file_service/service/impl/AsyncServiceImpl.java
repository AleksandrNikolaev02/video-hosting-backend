package com.example.file_service.service.impl;

import java.util.concurrent.ExecutorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.file_service.service.AsyncService;

@Component
public class AsyncServiceImpl implements AsyncService {
    private ExecutorService executorService;

    @Override
    public void excecuteTask(Runnable task) {
        executorService.submit(task);
    }

    @Autowired
    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }
}
