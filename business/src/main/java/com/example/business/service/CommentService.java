package com.example.business.service;

import com.example.business.dto.CreateCommentDTO;
import com.example.business.model.Comment;
import com.example.business.model.User;
import com.example.business.model.Video;
import com.example.business.repository.CommentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final FindEntityService findEntityService;

    @Transactional
    public void addComment(CreateCommentDTO dto, Long userId) {
        Video video = findEntityService.getVideoById(dto.videoId());
        User creator = findEntityService.getUserById(userId);

        // parent comment
        if (dto.commentId() == null) {
            Comment comment = new Comment();
            comment.setContent(dto.content());
            comment.setCreatedAt(LocalDateTime.now());
            comment.setCreator(creator);
            comment.setVideo(video);

            commentRepository.save(comment);

            return;
        }

        // sub comment
        Comment parent = findEntityService.getCommentById(dto.commentId());

        Comment subcomment = new Comment();
        subcomment.setContent(dto.content());
        subcomment.setCreatedAt(LocalDateTime.now());
        subcomment.setCreator(creator);
        subcomment.setVideo(video);

        subcomment.setParent(parent);
        parent.getComments().add(subcomment);

        commentRepository.save(subcomment);
    }
}
