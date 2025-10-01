package com.example.file_service.repository;

import com.example.file_service.model.PreviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PreviewEntityRepository extends JpaRepository<PreviewEntity, Long> {
    Optional<PreviewEntity> findByFilename(String filename);
}
