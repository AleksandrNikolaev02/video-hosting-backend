package com.example.business.repository;

import com.example.business.model.Channel;
import com.example.business.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChannelRepository extends JpaRepository<Channel, Long> {
    Optional<Channel> findByAuthor(User author);
}
