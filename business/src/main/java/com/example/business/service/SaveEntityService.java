package com.example.business.service;

import com.example.business.model.Video;
import com.example.business.repository.VideoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SaveEntityService {
    private final VideoRepository videoRepository;

    public void save(Video video) {
        videoRepository.save(video);
    }
}
