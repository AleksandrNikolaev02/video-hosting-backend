package com.example.business.repository;

import com.example.business.dto.GetEvaluatesVideoDTO;
import com.example.business.dto.PopularVideoDTO;
import com.example.business.model.Playlist;
import com.example.business.model.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface VideoRepository extends JpaRepository<Video, UUID> {
    @Query("""
        select video from Video video where video.creator.id = :user_id order by video.filename
    """)
    Page<Video> findByCreatorOrderByFilename(@Param("user_id") Long userId, Pageable pageable);

    @Query("""
        select video from Video video where video.filename IN :uids
    """)
    List<Video> findVideoByIds(@Param("uids") List<UUID> uids);

    @Query("""
        select video from Video video where video.channel.id = :channel_id and video.videoStatus = com.example.business.enums.VideoStatus.UPLOADED order by video.filename
    """)
    Page<Video> findByChannelAndUploadedStatus(@Param("channel_id") Long channelId, Pageable pageable);

    @Query(value = """
            select :video_id, l.likes, d.dislikes from (select COUNT(*)  as likes from reactions r where r.type = 'like' and r.video_id = :video_id) as l \s
            cross join \s
            (select COUNT(*) as dislikes from reactions r where r.type = 'dislike' and r.video_id = :video_id) as d;
            """, nativeQuery = true, name = "getAllEvaluatesByVideo")
    GetEvaluatesVideoDTO getAllEvaluatesByVideo(@Param("video_id") UUID videoId);

    List<Video> findByPlaylist(Playlist playlist, Pageable pageable);

    @Query(value = """
        SELECT video FROM Video video
            WHERE video.playlist.id = :playlistId
                AND video.videoStatus <> com.example.business.enums.VideoStatus.DELETED
    """,
    countQuery = """
        SELECT video FROM Video video
            WHERE video.playlist.id = :playlistId
                AND video.videoStatus <> com.example.business.enums.VideoStatus.DELETED
    """)
    Page<Video> findAllVideoByPlaylistBesidesDeleted(@Param("playlistId") Long playlistId, Pageable pageable);

    @Modifying
    @Query("DELETE FROM Video video WHERE video.channel.id = :channel_id")
    void deleteByChannelId(@Param("channel_id") Long channelId);


    @Query(value = """
            select video_id as videoId, count(*) as countLikes from reactions r \s
            where r.type = 'like' group by video_id order by countLikes desc
            """, nativeQuery = true)
    Page<PopularVideoDTO> getPopularVideo(Pageable pageable);
}
