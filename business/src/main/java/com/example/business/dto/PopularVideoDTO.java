package com.example.business.dto;

import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PopularVideoDTO {
    private UUID videoId;
    private Long countLikes;

    public PopularVideoDTO(UUID videoId, Long countLikes) {
        this.videoId = videoId;
        this.countLikes = countLikes;
    }
}
