package com.example.business.dto;

import com.example.business.enums.RequestChannelStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GetAllRequestsDTO {
    private Long requestId;
    private LocalDateTime createdAt;
    private RequestChannelStatus status;
    private Long channelId;
}
