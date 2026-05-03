package com.example.business.repository;

import com.example.business.model.Reaction;
import com.example.business.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    @Modifying
    @Query(value = "delete from reactions where reactions.type = 'dislike' and reactions.video_id = :filename",
           nativeQuery = true)
    void deleteDislikesByBatch(@Param("filename") UUID filename);

    @Modifying
    @Query(value = "delete from reactions where reactions.type = 'like' and reactions.video_id = :filename",
           nativeQuery = true)
    void deleteLikesByBatch(@Param("filename") UUID filename);

    @Modifying
    void deleteByUser(User user);
}
