package com.example.business.repository;

import com.example.business.model.Viewing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ViewingRepository extends JpaRepository<Viewing, Long> {

    @Modifying
    @Query(value = "DELETE FROM Viewing viewing WHERE viewing.video.filename = :filename")
    void deleteViewingByBatch(@Param("filename") UUID filename);
}
