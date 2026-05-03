package com.example.business.repository;

import com.example.business.enums.ChannelStatus;
import com.example.business.model.Channel;
import com.example.business.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChannelRepository extends JpaRepository<Channel, Long> {
    Optional<Channel> findByAuthor(User author);

    @Query(value = "SELECT channel FROM Channel channel WHERE channel.status = :status")
    List<Channel> findByStatus(@Param("status") ChannelStatus status);

    @Query(value = "SELECT channel FROM Channel channel WHERE channel.author.id = :authorId")
    Optional<Channel> findChannelByUserId(@Param("authorId") Long authorId);

    @Modifying
    @Query(value = """
        DELETE FROM Channel channel WHERE channel.id = :channel_id
            AND channel.playlists IS EMPTY AND channel.videos IS EMPTY
    """)
    void deleteChannel(@Param("channel_id") Long channelId);

    @Modifying
    void deleteByAuthor(User user);
}
