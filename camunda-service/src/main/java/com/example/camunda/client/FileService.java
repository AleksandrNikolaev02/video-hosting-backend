package com.example.camunda.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(value = "fileService", url = "http://localhost:8081")
public interface FileService {
    @DeleteMapping(value = "/channel/delete")
    ResponseEntity<Void> deleteChannel(@RequestHeader("X-user-id") Long userId,
                                       @RequestHeader("X-pipeline-key") String pipelineKey);
}
