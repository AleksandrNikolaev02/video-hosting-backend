package com.example.business.controller;

import com.example.business.dto.CreateCommentDTO;
import com.example.business.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/add")
    public ResponseEntity<Void> addComment(@RequestHeader("X-user-id") Long userId,
                                           @Validated @RequestBody CreateCommentDTO dto) {
        commentService.addComment(dto, userId);

        return ResponseEntity.ok().build();
    }
}
