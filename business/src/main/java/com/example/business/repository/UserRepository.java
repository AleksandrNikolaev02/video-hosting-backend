package com.example.business.repository;

import com.example.business.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT MAX(u.id) FROM User u")
    Long findMaxId();

    @Query("SELECT user FROM User user WHERE user.profile.email = :email")
    Optional<User> findUserByEmail(@Param("email") String email);
}
