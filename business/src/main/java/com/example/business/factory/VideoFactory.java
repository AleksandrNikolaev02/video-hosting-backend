package com.example.business.factory;

import com.example.business.dto.CreateBaseVideoDTO;
import com.example.business.enums.VideoStatus;
import com.example.business.model.User;
import com.example.business.model.Video;

public class VideoFactory {
    public static Video create(CreateBaseVideoDTO dto, User creator) {
        return Video.builder()
                .filename(dto.filename())
                .description(dto.description())
                .name(dto.title())
                .creator(creator)
                .videoStatus(VideoStatus.DRAFT)
                .build();
    }
}
