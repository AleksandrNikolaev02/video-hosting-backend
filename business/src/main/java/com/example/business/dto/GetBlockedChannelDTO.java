package com.example.business.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetBlockedChannelDTO {
    private Long channelId;
    private LocalDateTime timeBlock;
    private String message;
}
