package com.example.business.repository;

import com.example.business.model.Playlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    @Query(value = """
            SELECT playlist FROM Playlist playlist
                    WHERE playlist.owner.id = :playlistId
                            AND playlist.status <> com.example.business.enums.PlaylistStatus.DELETED
    """, countQuery = """
        SELECT COUNT(playlist) FROM Playlist playlist
                    WHERE playlist.owner.id = :userId
                            AND playlist.status <> com.example.business.enums.PlaylistStatus.DELETED
    """)
    Page<Playlist> findAllPlaylistByUserIdBesidesDeleted(@Param("userId") Long userId,
                                                         Pageable pageable);

    @Modifying
    @Query("""
        DELETE FROM Playlist p WHERE p.channel.id = :channelId AND p.videos IS EMPTY
    """)
    void deleteEmptyPlaylistsByChannelId(@Param("channelId") Long channelId);
}
