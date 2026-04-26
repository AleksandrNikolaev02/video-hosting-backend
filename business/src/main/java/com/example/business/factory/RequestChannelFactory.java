package com.example.business.factory;

import com.example.business.enums.RequestChannelStatus;
import com.example.business.model.RequestChannel;
import com.example.business.model.User;

import java.time.LocalDateTime;

public class RequestChannelFactory {
    public static RequestChannel create(User user, RequestChannelStatus status) {
        return RequestChannel.builder()
                .status(status)
                .creator(user)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
