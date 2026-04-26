package com.example.file_service.worker;

import com.example.file_service.enums.FileStatus;
import com.example.file_service.repository.PreviewEntityRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DeletePreviewWorker implements Worker {
    private final PreviewEntityRepository previewEntityRepository;

    @Override
    public void execute() {
        previewEntityRepository.cleanAllPreviewByStatus(FileStatus.DELETED);
    }
}
