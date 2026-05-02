package com.example.auth_service.service;

import com.example.auth_service.client.EmailServiceClient;
import com.example.auth_service.config.TopicConfig;
import com.example.auth_service.dto.LoginDTO;
import com.example.auth_service.dto.LoginResponse;
import com.example.auth_service.dto.RegisterDTO;
import com.example.auth_service.dto.ResponseTokenRefreshDTO;
import com.example.auth_service.enums.Role;
import com.example.auth_service.exceptions.KafkaSendMessageException;
import com.example.auth_service.exceptions.RoleNotFoundException;
import com.example.auth_service.exceptions.TwoFactorAuthenticationException;
import com.example.auth_service.exceptions.UserSettingNotFoundException;
import com.example.auth_service.model.RoleUser;
import com.example.auth_service.model.UserAuthInfo;
import com.example.auth_service.model.UserSetting;
import com.example.auth_service.repository.RoleRepository;
import com.example.auth_service.repository.UserSettingRepository;
import com.example.auth_service.util.JsonMapper;
import com.example.dto.CheckEmailDTO;
import com.example.dto.Status;
import com.example.dto.TwoFactorCodeDTO;
import com.example.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final UserSettingRepository userSettingRepository;
    private final UserService userService;
    private final TopicConfig config;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final EmailServiceClient emailServiceClient;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final JsonMapper mapper;

    public LoginResponse signIn(LoginDTO loginDTO) {
        authenticate(loginDTO);

        UserAuthInfo user = loadUserByUsername(loginDTO.email());

        UserSetting setting = userSettingRepository.findByUserId(user.getId())
                .orElseThrow(() -> new UserSettingNotFoundException(
                        String.format("UserSetting for user with id = %d not found!", user.getId())));

        LoginResponse response = new LoginResponse();

        if (setting.isTwoFactor()) {
            sendAsyncMessageToKafka(config.getEmailRequest(), loginDTO.email());

            response.setRequires2FA(true);
        } else {
            response.setPayload(generateJwtToken(user));
        }

        return response;
    }

    public ResponseTokenRefreshDTO signUp(RegisterDTO registerDTO) {
        RoleUser role = roleRepository.findByName(Role.USER)
                .orElseThrow(() -> new RoleNotFoundException("Role not found!"));

        UserAuthInfo user = createUserEntity(registerDTO, role);

        userService.saveUserInUserRepository(user);

        UserSetting setting = createUserSettingEntity(user);

        saveUserSettingInUserSettingRepository(setting);

        createUserInMicroservice(registerDTO, user);

        return generateJwtToken(user);
    }

    public ResponseTokenRefreshDTO twoFactorAuthentication(TwoFactorCodeDTO dto) {
        CheckEmailDTO emailDTO = sendSyncMessageRest(dto);

        validateStatus(emailDTO.getStatus());

        UserAuthInfo user = loadUserByUsername(emailDTO.getEmail());

        return generateJwtToken(user);
    }

    private void validateStatus(Status status) {
        if (status != Status.OK) {
            throw new TwoFactorAuthenticationException("Email microservice returned non ok status!");
        }
    }

    private CheckEmailDTO sendSyncMessageRest(TwoFactorCodeDTO dto) {
        ResponseEntity<CheckEmailDTO> response = emailServiceClient.checkEmail(dto);

        return response.getBody();
    }

    private UserAuthInfo createUserEntity(RegisterDTO registerDTO, RoleUser role) {
        return UserAuthInfo.builder()
                .role(role)
                .email(registerDTO.email())
                .password(passwordEncoder.encode(registerDTO.password()))
                .build();
    }

    private void authenticate(LoginDTO loginDTO) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDTO.email(),
                loginDTO.password()
        ));
    }

    private void sendAsyncMessageToKafka(String topic, String message) {
        kafkaTemplate.send(topic, message)
                .thenAcceptAsync(result -> log.info("Sent message: {} with offset {}", message,
                        result.getRecordMetadata().offset()))
                .exceptionallyAsync(error -> {
                    throw new KafkaSendMessageException("Error sending message in Kafka!");
                });
    }

    private ResponseTokenRefreshDTO generateJwtToken(UserAuthInfo user) {
        String jwt = jwtTokenProvider.generateToken(user);
        var refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return new ResponseTokenRefreshDTO(jwt, refreshToken.getToken());
    }

    private UserAuthInfo loadUserByUsername(String email) {
        return (UserAuthInfo) userService.loadUserByUsername(email);
    }

    private UserSetting createUserSettingEntity(UserAuthInfo user) {
        return UserSetting.builder()
                .user(user)
                .twoFactor(true)
                .build();
    }

    private void saveUserSettingInUserSettingRepository(UserSetting setting) {
        userSettingRepository.save(setting);
    }

    private void createUserInMicroservice(RegisterDTO dto, UserAuthInfo user) {
        kafkaTemplate.send(config.getCreateUser(), mapper.serialize(createUserDtoFrom(user, dto)));
    }

    private UserDTO createUserDtoFrom(UserAuthInfo user, RegisterDTO dto) {
        return UserDTO.builder()
                .id(user.getId())
                .fname(dto.firstName())
                .sname(dto.secondName())
                .email(user.getEmail())
                .build();
    }
}
