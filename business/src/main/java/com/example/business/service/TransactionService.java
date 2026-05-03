package com.example.business.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionService {
    @Transactional(propagation = Propagation.REQUIRED)
    public void requireTransaction(Runnable task) {
        task.run();
    }
}
