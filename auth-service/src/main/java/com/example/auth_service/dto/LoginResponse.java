package com.example.auth_service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
public class LoginResponse implements Serializable {
    private boolean requires2FA;
    private ResponseTokenRefreshDTO payload;
}
