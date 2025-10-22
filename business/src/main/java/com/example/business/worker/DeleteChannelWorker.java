package com.example.business.worker;

import com.example.business.enums.ChannelStatus;
import com.example.business.model.Channel;
import com.example.business.model.Dislike;
import com.example.business.model.Like;
import com.example.business.model.Playlist;
import com.example.business.model.Video;
import com.example.business.model.Viewing;
import com.example.business.repository.ChannelRepository;
import com.example.business.repository.PlaylistRepository;
import com.example.business.repository.ReactionRepository;
import com.example.business.repository.VideoRepository;
import com.example.business.repository.ViewingRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Component
@AllArgsConstructor
public class DeleteChannelWorker implements Worker {
    private final ExecutorService executorService;
    private ChannelRepository channelRepository;
    private VideoRepository videoRepository;
    private ViewingRepository viewingRepository;
    private ReactionRepository reactionRepository;
    private PlaylistRepository playlistRepository;

    @Override
    public void execute() {
        List<Channel> channels = channelRepository.findByStatus(ChannelStatus.DELETED);

        processVideos(channels);
        processPlaylists(channels);
        processChannels(channels);
    }

    private void processChannels(List<Channel> channels) {
        executorService.submit(() -> channels.forEach(channel -> {
            if (channel.getPlaylists().isEmpty() && channel.getVideos().isEmpty()) {
                channelRepository.delete(channel);
            }
        }));
    }

    private void processVideos(List<Channel> channels) {
        for (Channel channel : channels) {
            Collection<Video> videos = channel.getVideos();

            videos.forEach(video -> {
                if (!video.getViewings().isEmpty()) {
                    executorService.submit(() -> {
                        Collection<Viewing> viewings = video.getViewings();

                        viewingRepository.deleteAll(viewings);
                    });
                }

                if (!video.getLikes().isEmpty()) {
                    executorService.submit(() -> {
                        Collection<Like> likes = video.getLikes();

                        reactionRepository.deleteAll(likes);
                    });
                }

                if (!video.getDislikes().isEmpty()) {
                    executorService.submit(() -> {
                        Collection<Dislike> dislikes = video.getDislikes();

                        reactionRepository.deleteAll(dislikes);
                    });
                }

                if (video.getLikes().isEmpty() &&
                        video.getDislikes().isEmpty() && video.getViewings().isEmpty()) {
                    videoRepository.delete(video);
                }
            });
        }
    }

    private void processPlaylists(List<Channel> channels) {
        for (Channel channel : channels) {
            Collection<Playlist> playlists = channel.getPlaylists();

            List<Playlist> playlistsForDelete = new ArrayList<>();
            for (Playlist playlist : playlists) {
                if (playlist.getVideos().isEmpty()) {
                    playlistsForDelete.add(playlist);
                }
            }

            playlistRepository.deleteAll(playlistsForDelete);
        }
    }
}
