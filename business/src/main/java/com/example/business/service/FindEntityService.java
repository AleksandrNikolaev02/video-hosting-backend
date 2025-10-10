package com.example.business.service;

import com.example.business.exception.VideoNotFoundException;
import com.example.business.model.Video;
import com.example.business.repository.VideoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class FindEntityService {
    private final VideoRepository videoRepository;

    public Video getVideoById(Long videoId) {
        return videoRepository.findById(videoId).orElseThrow(()
                -> new VideoNotFoundException(String.format("Video with id %d not found!", videoId)));
    }
}
