package com.example.business.factory;

import com.example.business.dto.CreateChannelDTO;
import com.example.business.enums.ChannelStatus;
import com.example.business.model.Channel;
import com.example.business.model.User;

import java.time.LocalDateTime;

public class ChannelFactory {
    public static Channel create(User authorChannel, CreateChannelDTO dto) {
        return Channel.builder()
                .createdAt(LocalDateTime.now())
                .name(dto.name())
                .description(dto.description())
                .author(authorChannel)
                .status(ChannelStatus.ACTIVE)
                .countSubs(0)
                .build();
    }
}
