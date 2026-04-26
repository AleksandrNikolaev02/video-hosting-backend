package com.example.business.controller;

import com.example.business.dto.CreateBasePreviewDTO;
import com.example.business.dto.CreateBasePreviewResponseDTO;
import com.example.business.dto.DeletePreviewDTO;
import com.example.business.service.PreviewService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/preview")
public class PreviewController {
    private final PreviewService previewService;

    @Operation(summary = "Создать сущность превью")
    @PostMapping(value = "/create")
    public ResponseEntity<CreateBasePreviewResponseDTO> createPreview(@RequestBody CreateBasePreviewDTO dto,
                                              @RequestHeader("X-user-id") Long userId) {
        CreateBasePreviewResponseDTO response = previewService.createPreview(dto, userId);

        return ResponseEntity.status(201).body(response);
    }

    @Operation(summary = "Удалить сущность превью")
    @DeleteMapping(value = "/delete")
    public ResponseEntity<Void> deletePreview(@RequestBody DeletePreviewDTO dto,
                                              @RequestHeader("X-user-id") Long userId) throws JsonProcessingException {
        previewService.deletePreview(dto, userId);

        return ResponseEntity.status(204).build();
    }
}
