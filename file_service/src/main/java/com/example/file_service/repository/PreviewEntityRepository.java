package com.example.file_service.repository;

import com.example.file_service.model.PreviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PreviewEntityRepository extends JpaRepository<PreviewEntity, UUID> {
}
