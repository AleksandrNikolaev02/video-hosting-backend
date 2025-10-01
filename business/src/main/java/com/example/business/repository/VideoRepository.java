package com.example.business.repository;

import com.example.business.model.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VideoRepository extends JpaRepository<Video, Long> {
    Optional<Video> findVideoByPath(String filename);
    @Query(value = "select * from videos where videos.user_id = :user_id",
           nativeQuery = true)
    Page<Video> findAllVideoByUserId(@Param("user_id") Long userId, Pageable pageable);
}
