package com.example.email_service.repository;

import com.example.email_service.model.TwoFactorCode;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TwoFactorCodeRepository extends CrudRepository<TwoFactorCode, UUID> {
    @Query("SELECT code FROM TwoFactorCode code where code.email = :email")
    List<TwoFactorCode> findAllTwoFactorCodeByEmail(@Param("email") String email);

    @Modifying
    @Query(value = """
        DELETE FROM codes
        WHERE id IN (
            SELECT id FROM codes
            WHERE created_at < :threshold
            LIMIT :batchSize
        )
    """, nativeQuery = true)
    int deleteBatch(@Param("threshold") LocalDateTime threshold,
                    @Param("batchSize") int batchSize);
}
