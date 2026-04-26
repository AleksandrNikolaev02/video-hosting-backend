package com.example.business.service.cleanup;

import com.example.business.enums.EvaluateType;
import com.example.business.repository.ReactionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class ReactionCleanupService {
    private final ReactionRepository reactionRepository;

    @Transactional
    public void cleanup(EvaluateType type, UUID filename) {
        try {
            switch (type) {
                case LIKE -> reactionRepository.deleteLikesByBatch(filename);
                case DISLIKE -> reactionRepository.deleteDislikesByBatch(filename);
            }
        } catch (Exception e) {
            log.error("Ошибка при удалении реакций!");
        }
    }
}
