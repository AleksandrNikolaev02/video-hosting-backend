package com.example.auth_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public record ChangeRoleDTO(
        @NotNull(message = "User ID cannot be null")
        @Min(value = 1, message = "User ID must be greater than 0")
        Integer userId,
        @NotNull(message = "Role Name cannot be null")
        String roleName
) implements Serializable {
}
