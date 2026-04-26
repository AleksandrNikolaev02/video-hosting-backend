package com.example.business.service;

import com.example.business.model.Preview;
import com.example.business.model.Video;
import com.example.business.model.Viewing;
import com.example.business.repository.PreviewRepository;
import com.example.business.repository.VideoRepository;
import com.example.business.repository.ViewingRepository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SaveEntityService {
    private final VideoRepository videoRepository;
    private final ViewingRepository viewingRepository;
    private final PreviewRepository previewRepository;

    public void save(Video video) {
        videoRepository.save(video);
    }

    public void save(Viewing viewing) {
        viewingRepository.save(viewing);
    }

    public void save(Preview preview) {
        previewRepository.save(preview);
    }
}
