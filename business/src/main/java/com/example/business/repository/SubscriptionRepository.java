package com.example.business.repository;

import com.example.business.model.Channel;
import com.example.business.model.Subscription;
import com.example.business.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByChannelAndSubscriber(Channel channel, User subscriber);
    Page<Subscription> findBySubscriber(Pageable pageable, User subscriber);

    @Modifying
    void deleteBySubscriber(User subscriber);
}
