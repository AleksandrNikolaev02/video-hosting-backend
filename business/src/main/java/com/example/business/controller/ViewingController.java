package com.example.business.controller;

import com.example.business.service.ViewingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(value = "/viewing")
public class ViewingController {
    private final ViewingService viewingService;

    @Operation(summary = "Добавить просмотр к видео")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(description = "Означает, что пользователь уже смотрел это видео и добавить просмотр нельзя",
                    responseCode = "409")
    })
    @PostMapping(value = "/add/{video_id}")
    public ResponseEntity<Void> addViewing(@RequestHeader("User-Agent") String header,
                                           @RequestHeader(value = "X-user-id", required = false) Long userId,
                                           @PathVariable("video_id") UUID filename) {
        viewingService.addViewing(userId, header, filename);

        return ResponseEntity.ok().build();
    }
}
