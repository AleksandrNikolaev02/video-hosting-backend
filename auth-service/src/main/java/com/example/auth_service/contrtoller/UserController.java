package com.example.auth_service.contrtoller;

import com.example.auth_service.dto.LoginDTO;
import com.example.auth_service.dto.RegisterDTO;
import com.example.auth_service.dto.ResponseTokenRefreshDTO;
import com.example.auth_service.service.AuthenticationService;
import com.example.dto.TwoFactorCodeDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Validated @RequestBody LoginDTO loginDTO) {
        return ResponseEntity.ok(authenticationService.signIn(loginDTO));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Validated @RequestBody RegisterDTO registerDTO) {
        return ResponseEntity.status(200).body(authenticationService.signUp(registerDTO));
    }

    @PostMapping("/twoFactor")
    @Operation(summary = "Двухфакторная аутентификация")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseTokenRefreshDTO.class))}),
            @ApiResponse(responseCode = "401", description = "Email microservice returned non ok status!"),
            @ApiResponse(responseCode = "503", description = "Microservice email_service is unavailable now!")})
    @Tag(name = "public")
    public ResponseEntity<?> twoFactorAuthentication(@Validated @RequestBody TwoFactorCodeDTO dto) {
        return ResponseEntity.ok(authenticationService.twoFactorAuthentication(dto));
    }
}
