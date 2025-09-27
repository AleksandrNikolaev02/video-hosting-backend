package com.example.business.controller;

import dev.alex.auth.starter.auth_spring_boot_starter.annotation.Authorize;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @Authorize(value = "TEACHER")
    @GetMapping("/test")
    @Operation(summary = "Протестировать работу стартера для ролей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "403", description = "Not enough rights!", content = @Content)})
    public void test() {
        System.out.println("Passed!");
    }
}
