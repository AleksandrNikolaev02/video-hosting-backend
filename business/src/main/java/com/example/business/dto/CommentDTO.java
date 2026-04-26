package com.example.business.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CommentDTO {
    private Long id;
    private LocalDateTime createdAt;
    private String content;
    private Long creatorId;
    private boolean isBelong;
    private LikeDTO like;
    private DislikeDTO dislike;
}
