package com.example.auth_service.repository;

import com.example.auth_service.model.UserSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserSettingRepository extends JpaRepository<UserSetting, Integer> {
    @Query("SELECT u FROM UserSetting u WHERE u.user.id = :id")
    Optional<UserSetting> findByUserId(@Param("id") Integer id);
}
