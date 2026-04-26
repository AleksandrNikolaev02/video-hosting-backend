package com.example.business.repository;

import com.example.business.model.Preview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface PreviewRepository extends JpaRepository<Preview, Long> {
    @Modifying
    @Query(value = "DELETE FROM Preview preview WHERE preview.video.filename = :filename")
    void deletePreview(@Param("filename") UUID filename);
}
