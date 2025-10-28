package com.example.business.validator;

import com.example.business.model.Channel;
import com.example.business.model.Comment;
import com.example.business.model.Playlist;
import com.example.business.model.RequestChannel;
import com.example.business.model.User;
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

    public void validateChannelCreator(Channel channel, Long authorId) {
        if (!channel.getAuthor().getId().equals(authorId)) {
            throw new NoRightsException("You aren't creator of channel!");
        }
    }

    public void validateRequestChannelCreator(RequestChannel requestChannel, Long userId) {
        if (!requestChannel.getCreator().getId().equals(userId)) {
            throw new NoRightsException("You aren't creator of channel request!");
        }
    }

    public void validateCommentCreator(Comment comment, User user) {
        if (!comment.getCreator().getId().equals(user.getId())) {
            throw new NoRightsException("You aren't creator of comment!");
        }
    }
}
