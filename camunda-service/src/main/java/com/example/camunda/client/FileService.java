package com.example.camunda.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "fileService", url = "http://localhost:8081")
public interface FileService {
}
