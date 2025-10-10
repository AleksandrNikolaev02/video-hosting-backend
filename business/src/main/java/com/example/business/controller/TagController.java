package com.example.business.controller;

import com.example.business.dto.TagDTO;
import com.example.business.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/tag")
public class TagController {
    private final TagService tagService;

    @Operation(summary = "Добавить коллекцию тегов для видео")
    @PostMapping(value = "/add")
    public ResponseEntity<Void> addTags(@Validated @RequestBody TagDTO dto,
                                        @RequestHeader("X-user-id") Long userId) {
        tagService.addTags(dto, userId);

        return ResponseEntity.status(201).build();
    }

    @Operation(summary = "Удалить коллекцию тегов для видео")
    @DeleteMapping(value = "/delete")
    public ResponseEntity<Void> deleteTags(@Validated @RequestBody TagDTO dto,
                                           @RequestHeader("X-user-id") Long userId) {
        tagService.deleteTags(dto, userId);

        return ResponseEntity.status(204).build();
    }
}
