package com.example.business.controller;

import com.example.business.dto.CreateBaseVideoDTO;
import com.example.business.dto.CreateBaseVideoResponseDTO;
import com.example.business.dto.GetVideoDTO;
import com.example.business.dto.UpdatePathVideoDTO;
import com.example.business.dto.UpdateVideoDTO;
import com.example.business.mapper.VideoMapper;
import com.example.business.service.VideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/video")
public class VideoController {
    private final VideoService videoService;
    private final VideoMapper videoMapper;

    @PostMapping(value = "/create")
    @Operation(summary = "Создать базовое видео")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "404",
                         content = @Content(schema = @Schema(implementation = String.class)))
    })
    public ResponseEntity<CreateBaseVideoResponseDTO> createVideo(@Validated @RequestBody CreateBaseVideoDTO dto,
                                                                  @RequestHeader("X-user-id") Long userId) {
        return ResponseEntity.status(201).body(videoMapper.getCreateVideoResponseDtoFromVideo(
                videoService.createVideo(dto, userId)
        ));
    }

    @Operation(summary = "Опубликовать видео на сервисе")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404",
                         content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping(value = "/post")
    public ResponseEntity<Void> postVideo(@RequestParam("filename") String filename,
                                          @RequestHeader("X-user-id") Long userId) {
        videoService.postVideo(filename, userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/update")
    @Operation(summary = "Обновить видео после его загрузки в микросервис с файлами")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "404", description = "Video not found!")
    })
    public ResponseEntity<Void> updateVideo(@RequestBody UpdateVideoDTO dto,
                                            @RequestHeader("filename") String filename,
                                            @RequestHeader("X-user-id") Long userId) {
        videoService.updateVideo(dto, filename, userId);

        return ResponseEntity.status(204).build();
    }

    @PutMapping(value = "/update_path")
    @Operation(summary = "Синхронизировать имя видео с именем из микросервиса файлов")
    public ResponseEntity<Void> updatePathVideo(@Validated @RequestBody UpdatePathVideoDTO dto,
                                                @RequestHeader("X-user-id") Long userId) {
        videoService.updateVideoPath(dto, userId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Получить список всех видео по id пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = GetVideoDTO.class)))) })
    @GetMapping(value = "/get_videos")
    public Page<GetVideoDTO> getVideos(@RequestHeader("X-user-id") Long userId,
                                       @PageableDefault Pageable pageable) {
        return videoService.getVideos(userId, pageable).map(videoMapper::getVideoDtoFromVideo);
    }
}
