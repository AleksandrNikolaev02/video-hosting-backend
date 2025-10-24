package com.example.business.service.cleanup;

import com.example.business.repository.ChannelRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ChannelCleanupService {
    private final ChannelRepository channelRepository;

    @Transactional
    public void cleanup() {

    }
}
