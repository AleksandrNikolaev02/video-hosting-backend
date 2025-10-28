package com.example.business.mapper;

import com.example.business.dto.CommentDTO;
import com.example.business.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "creatorId", expression = "java(comment.getCreator().getId())")
    @Mapping(target = "like.count", expression = "java(comment.getLikes().size())")
    @Mapping(target = "dislike.count", expression = "java(comment.getDislikes().size())")
    CommentDTO getCommentDtoFromComment(Comment comment);
}
