package com.example.business.service.cleanup;

import com.example.business.repository.ViewingRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class ViewingCleanupService {
    private final ViewingRepository viewingRepository;

    @Transactional
    public void cleanup(UUID filename) {
        try {
            viewingRepository.deleteViewingByBatch(filename);
        } catch (Exception exception) {
            log.error("Ошибка при удалении просмотров!");
        }
    }
}
