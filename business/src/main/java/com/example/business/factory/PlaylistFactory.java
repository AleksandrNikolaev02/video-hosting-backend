package com.example.business.factory;

import com.example.business.model.Playlist;
import com.example.business.model.User;

import java.time.LocalDateTime;

public class PlaylistFactory {
    public static Playlist create(User owner, String name) {
        return Playlist.builder()
                .name(name)
                .owner(owner)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
