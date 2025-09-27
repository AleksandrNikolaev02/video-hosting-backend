package com.example.auth_service.repository;

import com.example.auth_service.model.RoleUser;
import com.example.auth_service.model.UserAuthInfo;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<UserAuthInfo, Integer> {
    Optional<UserAuthInfo> findByEmail(String email);
    List<UserAuthInfo> findByRole(RoleUser role);
}
