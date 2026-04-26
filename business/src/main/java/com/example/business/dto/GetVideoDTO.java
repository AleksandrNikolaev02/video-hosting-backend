package com.example.business.dto;

import com.example.business.enums.VideoStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetVideoDTO {
    private UUID filename;
    private Long userId;
    private String description;
    private String title;
    private String path;
    private VideoStatus videoStatus;
    private LocalDateTime date;
    private Integer countViewing;
    private PreviewDTO video_preview;
    private Set<String> tags;
    private Long channelId;
    private String channelName;
    private Integer subscribersCount;
}
