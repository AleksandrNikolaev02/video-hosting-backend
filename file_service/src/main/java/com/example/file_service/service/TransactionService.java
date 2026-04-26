package com.example.file_service.service;

public interface TransactionService {
    void requiredTransaction(Runnable task);
    void requiresNewTransaction(Runnable task);
}
