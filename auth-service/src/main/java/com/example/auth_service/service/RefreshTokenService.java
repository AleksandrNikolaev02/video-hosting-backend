package com.example.auth_service.service;

import com.example.auth_service.exceptions.RefreshTokenNotFoundException;
import com.example.auth_service.exceptions.TokenRefreshException;
import com.example.auth_service.exceptions.UserNotFoundException;
import com.example.auth_service.model.RefreshToken;
import com.example.auth_service.model.UserAuthInfo;
import com.example.auth_service.repository.RefreshTokenRepository;
import com.example.auth_service.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    @Value("${jwt.time-to-live-refresh-token}")
    private Integer timeToLiveRefreshToken;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshToken findByRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RefreshTokenNotFoundException("Refresh token not found!"));
    }

    @Transactional
    public RefreshToken createRefreshToken(Integer userId) {
        UserAuthInfo user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("User not found!"));

        if (user.getToken() != null) {
            if (!checkExpiration(user.getToken())) {
                return user.getToken();
            }

            refreshTokenRepository.delete(user.getToken());
        }

        RefreshToken refreshToken = createRefreshTokenEntityFromUser(user);
        saveRefreshTokenResult(user, refreshToken);
        return refreshToken;
    }

    private RefreshToken createRefreshTokenEntityFromUser(UserAuthInfo user) {
        return RefreshToken.builder()
                .user(user)
                .expiryDate(LocalDateTime.now().plusSeconds(timeToLiveRefreshToken))
                .token(UUID.randomUUID().toString())
                .build();
    }

    private void saveRefreshTokenResult(UserAuthInfo user, RefreshToken refreshToken) {
        user.setToken(refreshToken);
        refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public void verifyExpiration(RefreshToken token) {
        if (checkExpiration(token)) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException("Refresh token was expired!");
        }
    }

    private boolean checkExpiration(RefreshToken token) {
        return token.getExpiryDate().isBefore(LocalDateTime.now());
    }
}
