package com.example.email_service.controller;

import com.example.dto.CheckEmailDTO;
import com.example.dto.TwoFactorCodeDTO;
import com.example.email_service.service.TwoFactorCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/code")
public class TwoFactorCodeController {
    private TwoFactorCodeService factorCodeService;

    @PostMapping("/verify")
    public ResponseEntity<CheckEmailDTO> checkEmail(@RequestBody TwoFactorCodeDTO dto) {
        return ResponseEntity.ok(factorCodeService.verify(dto));
    }

    @Autowired
    public void setFactorCodeService(TwoFactorCodeService factorCodeService) {
        this.factorCodeService = factorCodeService;
    }
}
