package com.example.business.controller;

import com.example.business.dto.AddVideoInPlaylistDTO;
import com.example.business.dto.CreatePlaylistDTO;
import com.example.business.dto.DeletePlaylistDTO;
import com.example.business.dto.GetPlaylistsDTO;
import com.example.business.dto.GetVideoDTO;
import com.example.business.mapper.PlaylistMapper;
import com.example.business.mapper.VideoMapper;
import com.example.business.service.PlaylistService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/playlist")
@AllArgsConstructor
public class PlaylistController {
    private final PlaylistService playlistService;
    private final PlaylistMapper playlistMapper;
    private final VideoMapper videoMapper;

    @Operation(summary = "Создать пустой плейлист")
    @PostMapping(value = "/create")
    public ResponseEntity<Void> createPlaylist(@Validated @RequestBody CreatePlaylistDTO dto,
                                               @RequestHeader("X-user-id") Long userId) {
        playlistService.createPlaylist(dto, userId);

        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Удалить плейлист")
    @DeleteMapping(value = "/delete")
    public ResponseEntity<Void> deletePlaylist(@Validated @RequestBody DeletePlaylistDTO dto,
                                               @RequestHeader("X-user-id") Long userId) {
        playlistService.deletePlaylist(userId, dto);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Добавить видео в плейлист")
    @PostMapping(value = "/addVideo")
    public ResponseEntity<Void> addVideoInPlaylist(@Validated @RequestBody AddVideoInPlaylistDTO dto,
                                                   @RequestHeader("X-user-id") Long userId) {
        playlistService.addVideoInPlaylist(dto, userId);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Получить список плейлистов по пользователю")
    @GetMapping(value = "/getPlaylists")
    public List<GetPlaylistsDTO> getPlaylistsById(@RequestHeader("X-user-id") Long userId,
                                                  @PageableDefault Pageable pageable) {
        return playlistService.getAllPlaylistsByUser(userId, pageable)
                .stream()
                .map(playlistMapper::getPlaylistFromPlaylist)
                .toList();
    }

    @Operation(summary = "Получить все видео из плейлиста пользователя")
    @GetMapping(value = "/getVideosFromPlaylist/{id}")
    public List<GetVideoDTO> getVideosFromPlaylist(@RequestHeader("X-user-id") Long userId,
                                                   @PathVariable("id") Long playlistId,
                                                   @PageableDefault Pageable pageable) {
        return playlistService.getAllVideoFromPlaylist(userId, pageable, playlistId)
                .stream()
                .map(videoMapper::getVideoDtoFromVideo)
                .toList();
    }
}
