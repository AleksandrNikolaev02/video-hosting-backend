package com.example.file_service.worker;

import com.example.file_service.enums.FileStatus;
import com.example.file_service.repository.VideoEntityRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DeleteFilesWorker implements Worker {
    private final VideoEntityRepository videoEntityRepository;

    @Transactional
    public void execute() {
        videoEntityRepository.cleanAllFilesWithStatusDeleted(FileStatus.DELETED);
    }
}
