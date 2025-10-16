package com.example.business.repository;

import com.example.business.model.BlockedChannel;
import com.example.business.model.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BlockedChannelRepository extends JpaRepository<BlockedChannel, Long> {
    @Query("SELECT channel FROM BlockedChannel channel WHERE channel.timeBlock <= :now")
    List<BlockedChannel> findAllExpiredChannels(LocalDateTime now);

    Optional<BlockedChannel> findByChannel(Channel channel);
}
