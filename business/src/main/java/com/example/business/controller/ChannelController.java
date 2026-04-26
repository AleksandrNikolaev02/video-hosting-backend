package com.example.business.controller;

import com.example.business.dto.BlockedChannelDTO;
import com.example.business.dto.ChangeOwnerDTO;
import com.example.business.dto.ChannelDTO;
import com.example.business.dto.CreateChannelDTO;
import com.example.business.dto.GetAllRequestsDTO;
import com.example.business.dto.GetBlockedChannelDTO;
import com.example.business.dto.SendRequestDTO;
import com.example.business.dto.UpdateChannelDTO;
import com.example.business.mapper.ChannelMapper;
import com.example.business.service.ChannelService;
import dev.alex.auth.starter.auth_spring_boot_starter.annotation.Authorize;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/channel")
public class ChannelController {
    private final ChannelService channelService;
    private final ChannelMapper channelMapper;

    @Operation(summary = "Создать канал (если канал есть, то будет ошибка)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Канал успешно создан!"),
            @ApiResponse(responseCode = "409", description = "Пользователь уже имеет канал!")
    })
    @PostMapping("/create")
    public ResponseEntity<Void> createChannel(@RequestHeader("X-user-id") Long userId,
                                              @Validated @RequestBody CreateChannelDTO dto) {
        channelService.createChannel(dto, userId);

        return ResponseEntity.status(201).build();
    }

    @GetMapping("/my")
    @Operation(summary = "Проверить наличие канала пользователя")
    public ResponseEntity<Boolean> checkExistsChannel(@RequestHeader("X-user-id") Long userId) {
        return ResponseEntity.ok(channelService.checkExistsChannel(userId));
    }

    @GetMapping("/channel/{id}")
    @Operation(summary = "Получить информацию о канале по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Информация о канале получена!"),
            @ApiResponse(responseCode = "404", description = "Channel with id {id} not found!")
    })
    public ResponseEntity<ChannelDTO> getChannelInfo(@PathVariable("id") Long channelId) {
        return ResponseEntity.ok(channelService.getChannelInfo(channelId));
    }

    @PutMapping("/update")
    @Operation(summary = "Обновить данные канала")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Данные канала обновлены!")
    })
    public ResponseEntity<Void> updateChannel(@RequestHeader("X-user-id") Long userId,
                                              @Validated @RequestBody UpdateChannelDTO dto) {
        channelService.updateDataChannel(dto, userId);

        return ResponseEntity.status(204).build();
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Удалить канал пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Данные канала обновлены!")
    })
    public ResponseEntity<Void> deleteChannel(@RequestHeader("X-user-id") Long userId,
                                              @RequestHeader("X-pipeline-key") String pipelineKey) {
        channelService.deleteChannel(userId, pipelineKey);

        return ResponseEntity.status(204).build();
    }

    @DeleteMapping("/drop/{channel_id}")
    @Authorize("ADMIN")
    @Operation(summary = "Снести канал пользователю")
    public ResponseEntity<Void> forciblyDeleteChannel(@PathVariable("channel_id") Long channelId) {
        channelService.dropChannel(channelId);

        return ResponseEntity.status(204).build();
    }

    @PostMapping("/change_owner")
    @Authorize("ADMIN")
    @Operation(summary = "Передать владение каналом другому пользователю")
    public ResponseEntity<Void> changeOwnerChannel(@Validated @RequestBody ChangeOwnerDTO dto) {
        channelService.changeOwnerChannel(dto);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/send_request")
    @Operation(summary = "Отправить заявку на один из статусов")
    public ResponseEntity<Void> sendChannelRequest(@RequestHeader("X-user-id") Long userId,
                                                   @Validated @RequestBody SendRequestDTO dto) {
        channelService.sendRequest(dto, userId);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remove_request/{id}")
    @Operation(summary = "Отозвать заявку")
    public ResponseEntity<Void> removeChannelRequest(@PathVariable("id") Long requestId,
                                                     @RequestHeader("X-user-id") Long userId) {
        channelService.removeRequest(userId, requestId);

        return ResponseEntity.status(204).build();
    }

    @GetMapping("/get_user_requests")
    @Operation(summary = "Получить все заявки по пользователю")
    public List<GetAllRequestsDTO> getRequestsByUser(@RequestHeader("X-user-id") Long userId) {
        return channelService.getUserRequests(userId)
                .stream()
                .map(channelMapper::getAllRequestsDtoFromRequestChannel)
                .toList();
    }

    @GetMapping("/get_requests")
    @Authorize("ADMIN")
    @Operation(summary = "Получить заявки со всех пользователей")
    public List<GetAllRequestsDTO> getAllRequests(@PageableDefault Pageable pageable) {
        return channelService.getAllRequests(pageable)
                .stream()
                .map(channelMapper::getAllRequestsDtoFromRequestChannel)
                .toList();
    }

    @PostMapping("/block")
    @Authorize("ADMIN")
    @Operation(summary = "Заблокировать канал пользователю")
    public ResponseEntity<Void> blockChannel(@Validated @RequestBody BlockedChannelDTO dto) {
        channelService.blockVideo(dto);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/get_blocked_channels")
    @Authorize("ADMIN")
    @Operation(summary = "Получить список всех заблокированных каналов")
    public List<GetBlockedChannelDTO> getAllBlockedChannels(@PageableDefault Pageable pageable) {
        return channelService.getAllBlockedChannels(pageable)
                .map(channelMapper::getBlockedChannelDtoFromBlockedChannel)
                .toList();
    }

    @PostMapping("/unblock/{channel_id}")
    @Authorize("ADMIN")
    @Operation(summary = "Разблокировать канал пользователя")
    public ResponseEntity<Void> unblockChannel(@PathVariable("channel_id") Long channelId) {
        channelService.unblockChannel(channelId);

        return ResponseEntity.ok().build();
    }
}
