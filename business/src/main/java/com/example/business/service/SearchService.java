package com.example.business.service;

import com.example.business.model.ElasticVideo;
import com.example.business.repository.ElasticVideoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class SearchService {
    private final ElasticVideoRepository elasticVideoRepository;

    public List<ElasticVideo> searchVideo(String query) {
        return elasticVideoRepository.findVideosByInfo(query);
    }
}
