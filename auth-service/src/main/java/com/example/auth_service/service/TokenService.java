package com.example.auth_service.service;

import com.example.auth_service.exceptions.AccessDeniedException;
import com.example.dto.AuthorizationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtTokenProvider jwtTokenProvider;

    public AuthorizationResponse validate(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new AccessDeniedException("Token is not valid!");
        }

        return AuthorizationResponse.builder()
                .role(jwtTokenProvider.getRoleFromToken(token))
                .userId(jwtTokenProvider.getIdFromToken(token))
                .build();
    }
}
