package com.example.business.factory;

import com.example.business.dto.CreateCommentDTO;
import com.example.business.model.Comment;
import com.example.business.model.User;
import com.example.business.model.Video;

import java.time.LocalDateTime;

public class CommentFactory {
    public static Comment create(CreateCommentDTO dto, Video video, User creator) {
        return Comment.builder()
                .content(dto.content())
                .createdAt(LocalDateTime.now())
                .creator(creator)
                .video(video)
                .isEdit(false)
                .build();
    }
}
