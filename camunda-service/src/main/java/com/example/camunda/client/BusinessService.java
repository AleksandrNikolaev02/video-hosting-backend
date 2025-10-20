package com.example.camunda.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(value = "deleteChannel", url = "http://localhost:8080")
public interface BusinessService {
    @DeleteMapping(value = "/channel/delete")
    ResponseEntity<Void> deleteBusinessDataChannel(@RequestHeader("X-user-id") Long userId);
}
