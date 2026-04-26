package com.example.file_service.repository;

import org.springframework.stereotype.Repository;

import com.example.file_service.model.PartFile;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface PartFileRepository extends JpaRepository<PartFile, Long> {
    @Modifying
    @Query("DELETE FROM PartFile pf WHERE pf.file.filename = :fileId")
    void deleteByFileId(@Param("fileId") UUID fileId);
}
