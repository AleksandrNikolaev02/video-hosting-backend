package com.example.business.repository;

import com.example.business.model.Playlist;
import com.example.business.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    List<Playlist> findByOwner(User user, Pageable pageable);

    @Modifying
    @Query("""
        DELETE FROM Playlist p WHERE p.channel.id = :channelId AND p.videos IS EMPTY
    """)
    void deleteEmptyPlaylistsByChannelId(@Param("channelId") Long channelId);

}
