package com.example.auth_service.repository;

import com.example.auth_service.enums.Role;
import com.example.auth_service.model.RoleUser;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RoleRepository extends CrudRepository<RoleUser, Integer> {
    Optional<RoleUser> findByName(Role role);
}
