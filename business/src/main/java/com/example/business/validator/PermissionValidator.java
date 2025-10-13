package com.example.business.validator;

import com.example.business.model.Playlist;
import com.example.business.model.Video;
import dev.alex.auth.starter.auth_spring_boot_starter.exception.NoRightsException;
import org.springframework.stereotype.Component;

@Component
public class PermissionValidator {
    public void validateCreatorOfVideo(Video video, Long userId) {
        if (!video.getCreator().getId().equals(userId)) {
            throw new NoRightsException("You are not creator of video!");
        }
    }

    public void validatePlaylistCreator(Playlist playlist, Long ownerId) {
        if (!playlist.getOwner().getId().equals(ownerId)) {
            throw new NoRightsException("You aren't creator of playlist!");
        }
    }
}
