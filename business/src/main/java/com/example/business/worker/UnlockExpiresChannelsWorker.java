package com.example.business.worker;

import com.example.business.model.BlockedChannel;
import com.example.business.repository.BlockedChannelRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@AllArgsConstructor
public class UnlockExpiresChannelsWorker implements Worker {
    private final BlockedChannelRepository blockedChannelRepository;

    @Override
    @Transactional
    public void execute() {
        log.info("Search expired blocked channels...");

        List<BlockedChannel> channels = blockedChannelRepository.findAllExpiredChannels(LocalDateTime.now());

        blockedChannelRepository.deleteAll(channels);
    }
}
