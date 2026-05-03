package com.example.business.repository;

import com.example.business.model.RequestChannel;
import com.example.business.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

public interface RequestChannelRepository extends JpaRepository<RequestChannel, Long> {
    @Modifying
    void deleteByCreator(User creator);
}
