package com.example.auth_service.client;

import com.example.auth_service.config.ClientConfig;
import com.example.dto.CheckEmailDTO;
import com.example.dto.TwoFactorCodeDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "email-service", url = "email-service:8083", configuration = ClientConfig.class)
public interface EmailServiceClient {
    @RequestMapping(method = RequestMethod.POST, value = "code/verify")
    ResponseEntity<CheckEmailDTO> checkEmail(@RequestBody TwoFactorCodeDTO dto);
}
