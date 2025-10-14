package com.example.business.service;

import com.example.business.dto.AddVideoInPlaylistDTO;
import com.example.business.dto.CreatePlaylistDTO;
import com.example.business.dto.DeletePlaylistDTO;
import com.example.business.factory.PlaylistFactory;
import com.example.business.model.Playlist;
import com.example.business.model.User;
import com.example.business.model.Video;
import com.example.business.repository.PlaylistRepository;
import com.example.business.repository.VideoRepository;
import com.example.business.validator.PermissionValidator;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class PlaylistService {
    private final PlaylistRepository playlistRepository;
    private final FindEntityService findEntityService;
    private final PermissionValidator validator;
    private final VideoRepository videoRepository;

    public void createPlaylist(CreatePlaylistDTO dto, Long userId) {
        User user = findEntityService.getUserById(userId);

        Playlist playlist = PlaylistFactory.create(user, dto.name());

        playlistRepository.save(playlist);
    }

    public void addVideoInPlaylist(AddVideoInPlaylistDTO dto, Long userId) {
        Playlist playlist = findEntityService.getPlaylistById(dto.playlistId());

        validator.validatePlaylistCreator(playlist,userId);

        Video video = findEntityService.getVideoById(dto.filename());

        playlist.getVideos().add(video);
        video.setPlaylist(playlist);

        playlistRepository.save(playlist);
    }

    public List<Playlist> getAllPlaylistsByUser(Long userId, Pageable pageable) {
        User user = findEntityService.getUserById(userId);

        return playlistRepository.findByOwner(user, pageable);
    }

    public List<Video> getAllVideoFromPlaylist(Long userId, Pageable pageable, Long playlistId) {
        Playlist playlist = findEntityService.getPlaylistById(playlistId);

        validator.validatePlaylistCreator(playlist, userId);

        return videoRepository.findByPlaylist(playlist, pageable);
    }

    @Transactional
    public void deletePlaylist(Long userId, DeletePlaylistDTO dto) {
        Playlist playlist = findEntityService.getPlaylistById(dto.playlistId());

        validator.validatePlaylistCreator(playlist, userId);

        deleteLinkFromVideosOfPlaylist(playlist);

        playlistRepository.delete(playlist);
    }

    private void deleteLinkFromVideosOfPlaylist(Playlist playlist) {
        playlist.getVideos().forEach(video -> video.setPlaylist(null));
    }
}
