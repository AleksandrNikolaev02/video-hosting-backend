package com.example.business.repository;

import com.example.business.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT MAX(u.id) FROM User u")
    Long findMaxId();
}
