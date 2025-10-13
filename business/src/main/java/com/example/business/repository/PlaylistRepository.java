package com.example.business.repository;

import com.example.business.model.Playlist;
import com.example.business.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    List<Playlist> findByOwner(User user, Pageable pageable);
}
