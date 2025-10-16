package com.example.business.factory;

import com.example.business.dto.BlockedChannelDTO;
import com.example.business.model.BlockedChannel;
import com.example.business.model.Channel;

public class BlockedChannelFactory {
    public static BlockedChannel create(BlockedChannelDTO dto, Channel channel) {
        return BlockedChannel.builder()
                .message(dto.message())
                .timeBlock(dto.blockedTime())
                .channel(channel)
                .build();
    }
}
