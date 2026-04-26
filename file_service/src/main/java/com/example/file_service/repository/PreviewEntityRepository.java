package com.example.file_service.repository;

import com.example.file_service.enums.FileStatus;
import com.example.file_service.model.PreviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PreviewEntityRepository extends JpaRepository<PreviewEntity, UUID> {
    List<PreviewEntity> findByUserId(Long userId);

    @Query(value = "SELECT preview FROM PreviewEntity preview where preview.businessId = :filename")
    Optional<PreviewEntity> findByFilename(@Param("filename") UUID filename);

    @Modifying
    @Query(value = "DELETE FROM PreviewEntity previewEntity WHERE previewEntity.status = :status")
    void cleanAllPreviewByStatus(FileStatus status);
}
