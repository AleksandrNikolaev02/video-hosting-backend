package com.example.file_service.repository;

import com.example.file_service.enums.FileStatus;
import com.example.file_service.model.VideoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VideoEntityRepository extends JpaRepository<VideoEntity, UUID> {
    @Query(value = "select * from files where files.user_id = :user_id order by files.filename",
           nativeQuery = true)
    List<VideoEntity> findByUserId(@Param("user_id") Long userId);

    Optional<VideoEntity> findByKey(String key);

    @Query(value = "select COALESCE(max(files.id), 0) from files", nativeQuery = true)
    Long findMaxId();

    @Query(value = "select files.length from files where files.business_id = :filename",
           nativeQuery = true)
    Long getFileSize(@Param("filename") UUID filename);

    @Query(value = "select files.content_type from files where files.business_id = :filename",
           nativeQuery = true)
    String getContentTypeByFilename(UUID filename);

    @Modifying
    @Query(value = "DELETE FROM VideoEntity video WHERE video.status = :status")
    void cleanAllFilesWithStatusDeleted(@Param("status") FileStatus status);

    Optional<VideoEntity> findVideoEntityByBusinessId(UUID businessId);
}
