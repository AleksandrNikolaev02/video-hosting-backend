package com.example.file_service.service.impl;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.file_service.service.TransactionService;

@Component
public class TransactionServiceImpl implements TransactionService {

    @Override
    @Transactional
    public void requiredTransaction(Runnable task) {
        task.run();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void requiresNewTransaction(Runnable task) {
        task.run();
    }
}
