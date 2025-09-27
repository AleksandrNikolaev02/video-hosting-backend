package com.example.auth_service.contrtoller;

import com.example.auth_service.dto.RequestTokenRefreshDTO;
import com.example.auth_service.dto.ResponseTokenRefreshDTO;
import com.example.auth_service.service.JwtTokenProvider;
import com.example.auth_service.service.RefreshTokenService;
import com.example.auth_service.service.TokenService;
import com.example.dto.AuthorizationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/token")
public class TokenController {
    private final TokenService tokenService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @PostMapping(value = "/validate")
    public ResponseEntity<AuthorizationResponse> validate(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok().body(tokenService.validate(token.substring(7)));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Обновить токен пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseTokenRefreshDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Refresh token not found!"),
            @ApiResponse(responseCode = "403", description = "Refresh token was expired!")})
    public ResponseEntity<?> refresh(@Validated @RequestBody RequestTokenRefreshDTO dto) {
        String refreshToken = dto.token();
        var token = refreshTokenService.findByRefreshToken(refreshToken);

        refreshTokenService.verifyExpiration(token);

        return ResponseEntity.ok(ResponseTokenRefreshDTO.builder()
                .accessToken(jwtTokenProvider.generateToken(token.getUser()))
                .refreshToken(refreshToken)
                .build());
    }
}
