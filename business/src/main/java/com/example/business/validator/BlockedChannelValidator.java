package com.example.business.validator;

import com.example.business.exception.ChannelBlockedException;
import com.example.business.model.BlockedChannel;
import com.example.business.model.Channel;
import com.example.business.repository.BlockedChannelRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class BlockedChannelValidator {
    private final BlockedChannelRepository blockedChannelRepository;

    public void validate(Channel channel) {
        if (channel != null) {
            if (channelIsBlocked(channel)) {
                throw new ChannelBlockedException("Your channel is blocked!");
            }
        }
    }

    private boolean channelIsBlocked(Channel channel) {
        Optional<BlockedChannel> blockedChannel = blockedChannelRepository.findByChannel(channel);

        return blockedChannel.isPresent();
    }
}
