package com.example.business.repository;

import com.example.business.dto.GetEvaluatesVideoDTO;
import com.example.business.model.Playlist;
import com.example.business.model.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VideoRepository extends JpaRepository<Video, Long> {
    Optional<Video> findVideoByPath(String filename);

    @Query(value = "select * from videos where videos.user_id = :user_id order by videos.path",
           nativeQuery = true)
    Page<Video> findAllVideoByUserId(@Param("user_id") Long userId, Pageable pageable);

    @Query(value = """
            select :video_id, l.likes, d.dislikes from (select COUNT(*)  as likes from reactions r where r.type = 'like' and r.video_id = :video_id) as l \s
            cross join \s
            (select COUNT(*) as dislikes from reactions r where r.type = 'dislike' and r.video_id = :video_id) as d;
            """, nativeQuery = true, name = "getAllEvaluatesByVideo")
    GetEvaluatesVideoDTO getAllEvaluatesByVideo(@Param("video_id") Long videoId);

    List<Video> findByPlaylist(Playlist playlist, Pageable pageable);
}
