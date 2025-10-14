package com.example.business.service;

import com.example.business.exception.PlaylistNotFoundException;
import com.example.business.exception.UserNotFoundException;
import com.example.business.exception.VideoNotFoundException;
import com.example.business.model.Playlist;
import com.example.business.model.User;
import com.example.business.model.Video;
import com.example.business.repository.PlaylistRepository;
import com.example.business.repository.UserRepository;
import com.example.business.repository.VideoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class FindEntityService {
    private final VideoRepository videoRepository;
    private final UserRepository userRepository;
    private final PlaylistRepository playlistRepository;

    public Video getVideoById(UUID filename) {
        return videoRepository.findById(filename).orElseThrow(()
                -> new VideoNotFoundException(String.format("Video with id %s not found!", filename)));
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(()
                -> new UserNotFoundException(String.format("User with id %d not found!", userId)));
    }

    public Playlist getPlaylistById(Long playlistId) {
        return playlistRepository.findById(playlistId).orElseThrow(()
                -> new PlaylistNotFoundException(String.format("Playlist with id %d not found!", playlistId)));
    }
}
