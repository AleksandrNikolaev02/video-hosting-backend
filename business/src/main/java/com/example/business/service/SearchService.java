package com.example.business.service;

import com.example.business.dto.GetVideoDTO;
import com.example.business.event.VideoDeleteEvent;
import com.example.business.mapper.VideoMapper;
import com.example.business.model.ElasticVideo;
import com.example.business.repository.ElasticVideoRepository;
import com.example.business.repository.VideoRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class SearchService {
    private final ElasticVideoRepository elasticVideoRepository;
    private final VideoRepository videoRepository;
    private final VideoMapper videoMapper;

    public List<GetVideoDTO> searchVideo(String query) {
        List<ElasticVideo> videos = elasticVideoRepository.findVideosByInfo(query);

        List<UUID> uids = videos.stream().map(video -> video.getFilename()).toList();

        return videoRepository.findAllById(uids).stream().map(videoMapper::getVideoDtoFromVideo).toList();
    }

    @TransactionalEventListener
    public void handleDeleteVideo(VideoDeleteEvent event) {
        elasticVideoRepository.deleteById(event.getFilename());
    }
}
