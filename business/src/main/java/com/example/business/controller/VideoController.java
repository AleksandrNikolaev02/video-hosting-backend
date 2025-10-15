package com.example.business.controller;

import com.example.business.dto.BelongEvaluateDTO;
import com.example.business.dto.CreateBaseVideoDTO;
import com.example.business.dto.CreateBaseVideoResponseDTO;
import com.example.business.dto.DeleteVideoDTO;
import com.example.business.dto.EvaluateVideoDTO;
import com.example.business.dto.GetEvaluatesVideoDTO;
import com.example.business.dto.GetVideoDTO;
import com.example.business.dto.RequestBelongEvaluateDTO;
import com.example.business.dto.UpdatePathVideoDTO;
import com.example.business.dto.UpdateVideoDTO;
import com.example.business.mapper.VideoMapper;
import com.example.business.model.ElasticVideo;
import com.example.business.service.SearchService;
import com.example.business.service.VideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/video")
public class VideoController {
    private final VideoService videoService;
    private final VideoMapper videoMapper;
    private final SearchService searchService;

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

    @DeleteMapping(value = "/delete")
    @Operation(summary = "Удалить видео")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    public ResponseEntity<Void> deleteVideo(@RequestHeader("X-user-id") Long userId,
                                            @RequestBody DeleteVideoDTO dto) {
        videoService.deleteVideo(dto, userId);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Опубликовать видео на сервисе")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404",
                         content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping(value = "/post")
    public ResponseEntity<Void> postVideo(@RequestParam("filename") UUID filename,
                                          @RequestHeader("X-user-id") Long userId) {
        videoService.postVideo(filename, userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/update/{filename}")
    @Operation(summary = "Обновить видео после его загрузки в микросервис с файлами")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "404", description = "Video not found!")
    })
    public ResponseEntity<Void> updateVideo(@RequestBody UpdateVideoDTO dto,
                                            @PathVariable("filename") UUID filename,
                                            @RequestHeader("X-user-id") Long userId) {
        videoService.updateVideo(dto, filename, userId);

        return ResponseEntity.status(204).build();
    }

    @Deprecated
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
    public List<GetVideoDTO> getVideos(@RequestHeader("X-user-id") Long userId,
                                       @PageableDefault Pageable pageable) {
        return videoService.getVideos(userId, pageable)
                .getContent().stream().map(videoMapper::getVideoDtoFromVideo).toList();
    }

    @Operation(summary = "Получить лайки и дизлайки конкретного видео")
    @GetMapping(value = "/get_evaluates/{filename}")
    public ResponseEntity<GetEvaluatesVideoDTO> getEvaluates(@PathVariable("filename") UUID filename) {
        return ResponseEntity.ok(videoService.getEvaluates(filename));
    }

    @Operation(summary = "Проверить принадлежность лайка и дизлайка видео")
    @PostMapping(value = "/check_evaluate")
    public ResponseEntity<BelongEvaluateDTO> checkBelongEvaluate(@RequestHeader("X-user-id") Long userId,
                                                                 @RequestBody RequestBelongEvaluateDTO dto) {
        return ResponseEntity.ok(videoService.checkBelongEvaluate(dto, userId));
    }

    @Operation(summary = "Поставить реакцию на видео")
    @PostMapping(value = "/react")
    public ResponseEntity<Void> reactVideo(@RequestHeader("X-user-id") Long userId,
                                           @RequestBody EvaluateVideoDTO dto) {
        videoService.evaluateVideo(dto, userId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Найти видео по запросу пользователя")
    @GetMapping(value = "/search")
    public List<ElasticVideo> searchVideo(@RequestParam(name = "query") String query) {
        return searchService.searchVideo(query)
                .stream()
                .peek(video -> {
                    if (video.getNames() == null) {
                        video.setNames(Collections.emptyList());
                    } else {
                        video.setNames(Arrays.stream(video.getNames()
                                        .get(0)
                                        .split(", "))
                                .toList());
                    }
                }).toList();
    }
}
