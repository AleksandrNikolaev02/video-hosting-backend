package com.example.business.service;

import com.example.business.exception.ChannelNotFoundException;
import com.example.business.exception.CommentNotFoundException;
import com.example.business.exception.PlaylistNotFoundException;
import com.example.business.exception.RequestNotFoundException;
import com.example.business.exception.UserNotFoundException;
import com.example.business.exception.VideoNotFoundException;
import com.example.business.model.BlockedChannel;
import com.example.business.model.Channel;
import com.example.business.model.Comment;
import com.example.business.model.Playlist;
import com.example.business.model.RequestChannel;
import com.example.business.model.User;
import com.example.business.model.Video;
import com.example.business.repository.BlockedChannelRepository;
import com.example.business.repository.ChannelRepository;
import com.example.business.repository.CommentRepository;
import com.example.business.repository.PlaylistRepository;
import com.example.business.repository.RequestChannelRepository;
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
    private final ChannelRepository channelRepository;
    private final RequestChannelRepository requestChannelRepository;
    private final BlockedChannelRepository blockedChannelRepository;
    private final CommentRepository commentRepository;

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
                -> new PlaylistNotFoundException(
                        String.format("Playlist with id %d not found!", playlistId))
                );
    }

    public Channel getChannelById(Long channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelNotFoundException(
                        String.format("Channel with id %d not found!", channelId))
                );
    }

    public Channel getChannelByUserId(Long userId) {
        return channelRepository.findChannelByUserId(userId)
                .orElseThrow(() -> new ChannelNotFoundException(
                        String.format("Channel with author_id %d not found!", userId))
                );
    }

    public RequestChannel getRequestChannelById(Long requestId) {
        return requestChannelRepository.findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException(
                        String.format("Request with id %d not found!", requestId))
                );
    }

    public BlockedChannel getBlockedChannelById(Long channelId) {
        return blockedChannelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelNotFoundException(
                        String.format("Blocked channel with id %d not found", channelId)
                ));
    }

    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(
                        String.format("Comment with id=%d not found", commentId)
                ));
    }

    public Channel getChannelByCreator(User creator) {
        return channelRepository.findByAuthor(creator)
                .orElseThrow(() -> new ChannelNotFoundException(
                        String.format("Channel with author id %d not found!", creator.getId())
                ));
    }
}
