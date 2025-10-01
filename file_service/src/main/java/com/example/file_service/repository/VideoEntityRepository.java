package com.example.file_service.repository;

import com.example.file_service.model.VideoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VideoEntityRepository extends JpaRepository<VideoEntity, Long> {
    List<VideoEntity> findByUserId(Long userId);
    Optional<VideoEntity> findByKey(Long key);
    @Query(value = "select COALESCE(max(files.id), 0) from files", nativeQuery = true)
    Long findMaxId();
    @Query(value = "select files.length from files where files.filename = :filename",
           nativeQuery = true)
    Long getFileSize(@Param("filename") String filename);
    @Query(value = "select files.content_type from files where files.filename = :filename",
           nativeQuery = true)
    String getContentTypeByFilename(String filename);
}
