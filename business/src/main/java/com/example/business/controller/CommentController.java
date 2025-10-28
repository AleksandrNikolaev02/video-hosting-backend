package com.example.business.controller;

import com.example.business.dto.CommentDTO;
import com.example.business.dto.CreateCommentDTO;
import com.example.business.dto.EditCommentDTO;
import com.example.business.dto.EvaluateCommentDTO;
import com.example.business.dto.GetSubCommentsDTO;
import com.example.business.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;

    @Operation(summary = "Добавление комментария к видео")
    @PostMapping("/add")
    public ResponseEntity<Void> addComment(@RequestHeader("X-user-id") Long userId,
                                           @Validated @RequestBody CreateCommentDTO dto) {
        commentService.addComment(dto, userId);

        return ResponseEntity.status(201).build();
    }

    @Operation(summary = "Получить комментарии к видео")
    @GetMapping("/get-comments-video/{filename}")
    public List<CommentDTO> getComments(@PageableDefault Pageable pageable,
                                        @PathVariable("filename") UUID filename,
                                        @RequestHeader(value = "X-user-id", required = false) Long userId) {
        return commentService.getComments(filename, pageable, userId);
    }

    @Operation(summary = "Получить подкомметарии к комментарию видео")
    @PostMapping("/get-sub-comments-video")
    public List<CommentDTO> getSubComments(@RequestBody GetSubCommentsDTO dto,
                                           @PageableDefault Pageable pageable,
                                           @RequestHeader(value = "X-user-id", required = false) Long userId) {
        return commentService.getSubComments(dto, pageable, userId);
    }

    @Operation(summary = "Отредактировать комментарий к видео")
    @PutMapping("/edit")
    public ResponseEntity<Void> editComment(@RequestBody EditCommentDTO dto,
                                            @RequestHeader("X-user-id") Long userId) {
        commentService.editComment(dto, userId);

        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Удалить комментарий к видео")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteComment(@RequestHeader("X-user-id") Long userId,
                                              @PathVariable("id") Long id) {
        commentService.deleteComment(userId, id);

        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Оценить комментарий")
    @PostMapping("/react")
    public ResponseEntity<Void> evaluateComment(@RequestHeader("X-user-id") Long userId,
                                                @Validated @RequestBody EvaluateCommentDTO dto) {
        commentService.evaluateComment(dto, userId);

        return ResponseEntity.ok().build();
    }
}
