package com.example.business.service.cleanup;

import com.example.business.repository.VideoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class VideoCleanupService {
    private final VideoRepository videoRepository;

    @Transactional
    public void cleanup(UUID filename) {
        try {
            videoRepository.deleteById(filename);
        } catch (Exception e) {
            log.error("Ошибка при удалении видео!");
        }
    }
}
