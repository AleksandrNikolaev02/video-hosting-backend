package com.example.business.repository;

import com.example.business.model.Comment;
import com.example.business.model.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findCommentsByVideoAndParentNull(Pageable pageable, Video video);

    Page<Comment> findCommentsByVideoAndParent(Pageable pageable, Video video, Comment comment);

    @Query(value = """
        SELECT COUNT(*) FROM reactions
                LEFT JOIN comments ON comments.id = reactions.comment_id
                        WHERE reactions.video_id IS NULL AND reactions.comment_id = :commentId
                                AND reactions.user_id = :userId AND reactions.type = :type
        """, nativeQuery = true)
    Long checkBelongReactionToComment(@Param("commentId") Long commentId,
                                      @Param("userId") Long userId,
                                      @Param("type") String type);
}
