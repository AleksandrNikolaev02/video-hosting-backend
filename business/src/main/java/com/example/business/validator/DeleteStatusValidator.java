package com.example.business.validator;

import com.example.business.enums.ChannelStatus;
import com.example.business.enums.PlaylistStatus;
import com.example.business.enums.VideoStatus;
import com.example.business.exception.ChannelAlreadyDeletedException;
import com.example.business.exception.PlaylistAlreadyDeletedException;
import com.example.business.exception.VideoAlreadyDeletedException;
import com.example.business.model.Channel;
import com.example.business.model.Playlist;
import com.example.business.model.Video;
import org.springframework.stereotype.Component;

@Component
public class DeleteStatusValidator {
    public void validate(Channel channel) {
        if (channel.getStatus().equals(ChannelStatus.DELETED)) {
            throw new ChannelAlreadyDeletedException("Channel has been already delete!");
        }
    }

    public void validate(Playlist playlist) {
        if (playlist.getStatus().equals(PlaylistStatus.DELETED)) {
            throw new PlaylistAlreadyDeletedException("Playlist has been already delete!");
        }
    }

    public void validate(Video video) {
        if (video.getVideoStatus().equals(VideoStatus.DELETED)) {
            throw new VideoAlreadyDeletedException("Video has been already delete!");
        }
    }
}
