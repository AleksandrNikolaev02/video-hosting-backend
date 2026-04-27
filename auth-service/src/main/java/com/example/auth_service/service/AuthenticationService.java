package com.example.auth_service.service;

import com.example.auth_service.config.TopicConfig;
import com.example.auth_service.dto.LoginDTO;
import com.example.auth_service.dto.RegisterDTO;
import com.example.auth_service.dto.ResponseTokenRefreshDTO;
import com.example.auth_service.enums.Role;
import com.example.auth_service.exceptions.KafkaSendMessageException;
import com.example.auth_service.exceptions.MicroserviceUnavailableException;
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
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final UserSettingRepository userSettingRepository;
    private final UserService userService;
    private final TopicConfig config;
    private final ReplyingKafkaTemplate<String, String, String> replyingKafkaTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final JsonMapper mapper;
    private final Integer TIMEOUT = 5;

    public Object signIn(LoginDTO loginDTO) {
        authenticate(loginDTO);

        UserAuthInfo user = loadUserByUsername(loginDTO.email());

        UserSetting setting = userSettingRepository.findByUserId(user.getId())
                .orElseThrow(() -> new UserSettingNotFoundException(
                        String.format("UserSetting for user with id = %d not found!", user.getId())));

        if (setting.isTwoFactor()) {
            sendAsyncMessageToKafka(config.getEmailRequest(), loginDTO.email());

            return "A code has been sent to your email!";
        }

        return generateJwtToken(user);
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
        var future = sendSyncMessageToKafka(config.getGetEmail(), dto);
        var response = getResponseFromEmailMicroservice(future);

        CheckEmailDTO emailDTO = mapper.deserialize(response.value(), CheckEmailDTO.class);

        validateStatus(emailDTO.getStatus());

        UserAuthInfo user = loadUserByUsername(emailDTO.getEmail());

        return generateJwtToken(user);
    }

    private void validateStatus(Status status) {
        if (status != Status.OK) {
            throw new TwoFactorAuthenticationException("Email microservice returned non ok status!");
        }
    }

    private ConsumerRecord<String, String> getResponseFromEmailMicroservice(
            RequestReplyFuture<String, String, String> future) {
        try {
            return future.get(TIMEOUT, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new MicroserviceUnavailableException("Microservice email_service is unavailable now!");
        }
    }

    private RequestReplyFuture<String, String, String> sendSyncMessageToKafka(String topic, TwoFactorCodeDTO dto) {
        String json = mapper.serialize(dto);

        ProducerRecord<String, String> record = new ProducerRecord<>(topic, json);

        return replyingKafkaTemplate.sendAndReceive(record);
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
