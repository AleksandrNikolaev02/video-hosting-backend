package com.example.business.service;

import com.example.business.dto.CommentDTO;
import com.example.business.dto.CreateCommentDTO;
import com.example.business.dto.EditCommentDTO;
import com.example.business.dto.EvaluateCommentDTO;
import com.example.business.dto.GetSubCommentsDTO;
import com.example.business.factory.CommentFactory;
import com.example.business.mapper.CommentMapper;
import com.example.business.model.Comment;
import com.example.business.model.User;
import com.example.business.model.Video;
import com.example.business.repository.CommentRepository;
import com.example.business.validator.PermissionValidator;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final FindEntityService findEntityService;
    private final PermissionValidator validator;
    private final EvaluateService evaluateService;
    private final CommentMapper commentMapper;

    @Transactional
    public void addComment(CreateCommentDTO dto, Long userId) {
        Video video = findEntityService.getVideoById(dto.videoId());
        User creator = findEntityService.getUserById(userId);

        // parent comment
        if (dto.commentId() == null) {
            Comment comment = CommentFactory.create(dto, video, creator);

            commentRepository.save(comment);

            return;
        }

        // sub comment
        Comment parent = findEntityService.getCommentById(dto.commentId());

        Comment subcomment = CommentFactory.create(dto, video, creator);

        subcomment.setParent(parent);
        parent.getComments().add(subcomment);

        commentRepository.save(subcomment);
    }

    public List<CommentDTO> getComments(UUID filename, Pageable pageable, Long userId) {
        Video video = findEntityService.getVideoById(filename);

        return commentRepository.findCommentsByVideoAndParentNull(pageable, video)
                .stream()
                .map(commentMapper::getCommentDtoFromComment)
                .peek(dto -> updateCommentDtoByExpression(dto, userId))
                .toList();
    }

    public List<CommentDTO> getSubComments(GetSubCommentsDTO dto, Pageable pageable, Long userId) {
        Video video = findEntityService.getVideoById(dto.filename());
        Comment parent = findEntityService.getCommentById(dto.parentId());

        return commentRepository.findCommentsByVideoAndParent(pageable, video, parent)
                .stream()
                .map(commentMapper::getCommentDtoFromComment)
                .peek(commentDTO -> updateCommentDtoByExpression(commentDTO, userId))
                .toList();
    }

    public void editComment(EditCommentDTO dto, Long userId) {
        User user = findEntityService.getUserById(userId);
        Comment comment = findEntityService.getCommentById(dto.commentId());

        validator.validateCommentCreator(comment, user);

        comment.setContent(dto.content());
        comment.setEdit(true);

        commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        User user = findEntityService.getUserById(userId);
        Comment comment = findEntityService.getCommentById(commentId);

        validator.validateCommentCreator(comment, user);

        commentRepository.delete(comment);
    }

    public void evaluateComment(EvaluateCommentDTO dto, Long userId) {
        Comment comment = findEntityService.getCommentById(dto.commentId());

        evaluateService.evaluate(dto.evaluateType(), userId, comment);
    }

    private void updateCommentDtoByExpression(CommentDTO dto, Long userId) {
        if (dto.getCreatorId().equals(userId)) {
            dto.setBelong(true);
        }

        if (commentRepository.checkBelongReactionToComment(dto.getId(), userId, "like") > 0) {
            dto.getLike().setBelong(true);
            return;
        }

        if (commentRepository.checkBelongReactionToComment(dto.getId(), userId, "dislike") > 0) {
            dto.getDislike().setBelong(true);
        }
    }
}
