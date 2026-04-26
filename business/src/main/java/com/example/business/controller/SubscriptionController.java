package com.example.business.controller;

import com.example.business.dto.ChannelDTO;
import com.example.business.dto.SubscribeDTO;
import com.example.business.dto.UnsubscribeDTO;
import com.example.business.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/subscription")
@AllArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @Operation(summary = "Подписаться на канал пользователя")
    @PostMapping("/subscribe")
    public ResponseEntity<Void> subscribe(@RequestHeader("X-user-id") Long userId,
                                          @Validated @RequestBody SubscribeDTO dto) {
        subscriptionService.subscribe(dto, userId);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Отписаться от канала пользователя")
    @PostMapping("/unsubscribe")
    public ResponseEntity<Void> unsubscribe(@RequestHeader("X-user-id") Long userId,
                                            @Validated @RequestBody UnsubscribeDTO dto) {
        subscriptionService.unsubscribe(dto, userId);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Получить список каналов, на которые подписался пользователь")
    @GetMapping("/list-channels")
    public List<ChannelDTO> getListChannelsBy(@RequestHeader("X-user-id") Long userId,
                                              @PageableDefault Pageable pageable) {
        return subscriptionService.getListChannels(pageable, userId);
    }
}
