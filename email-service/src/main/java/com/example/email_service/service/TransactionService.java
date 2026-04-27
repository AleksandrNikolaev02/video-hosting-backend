package com.example.email_service.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionService {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void requireNewTransaction(Runnable task) {
        task.run();
    }
}
