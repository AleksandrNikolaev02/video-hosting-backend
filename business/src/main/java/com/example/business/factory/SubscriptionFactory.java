package com.example.business.factory;

import com.example.business.model.Channel;
import com.example.business.model.Subscription;
import com.example.business.model.User;

import java.time.LocalDateTime;

public class SubscriptionFactory {
    public static Subscription create(User subscriber, Channel channel) {
        return Subscription.builder()
                .subscriber(subscriber)
                .channel(channel)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
