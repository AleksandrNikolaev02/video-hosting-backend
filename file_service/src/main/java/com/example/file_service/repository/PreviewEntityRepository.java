package com.example.file_service.repository;

import com.example.file_service.enums.FileStatus;
import com.example.file_service.model.PreviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface PreviewEntityRepository extends JpaRepository<PreviewEntity, UUID> {
    List<PreviewEntity> findByUserId(Long userId);

    @Modifying
    @Query(value = "DELETE FROM PreviewEntity previewEntity WHERE previewEntity.status = :status")
    void cleanAllPreviewByStatus(FileStatus status);
}
