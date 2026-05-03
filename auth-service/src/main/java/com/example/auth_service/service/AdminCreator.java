package com.example.auth_service.service;

import com.example.auth_service.config.AdminData;
import com.example.auth_service.config.TopicConfig;
import com.example.auth_service.enums.Role;
import com.example.auth_service.exceptions.RoleNotFoundException;
import com.example.auth_service.model.RoleUser;
import com.example.auth_service.model.UserAuthInfo;
import com.example.auth_service.model.UserSetting;
import com.example.auth_service.repository.RoleRepository;
import com.example.auth_service.repository.UserRepository;
import com.example.auth_service.repository.UserSettingRepository;
import com.example.dto.UserDTO;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminCreator {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserSettingRepository userSettingRepository;
    private final AdminData adminData;
    private final TopicConfig config;
    private final KafkaTemplate<String, UserDTO> kafkaTemplate;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void create() {
        RoleUser role = roleRepository.findByName(Role.ADMIN)
                .orElseThrow(() -> new RoleNotFoundException("Role ADMIN not found!"));

        if (userRepository.findByRole(role).isEmpty()) {
            UserAuthInfo user = UserAuthInfo.builder()
                    .email(adminData.getEmail())
                    .password(passwordEncoder.encode(adminData.getPassword()))
                    .role(role)
                    .enabled(true)
                    .build();

            userRepository.save(user);

            UserSetting userSetting = UserSetting.builder()
                    .user(user)
                    .twoFactor(false)
                    .build();

            userSettingRepository.save(userSetting);

            UserDTO userDTO = createUserDtoFromUserAuthInfo(user);
            kafkaTemplate.send(config.getCreateUser(), userDTO);
        }
    }

    private UserDTO createUserDtoFromUserAuthInfo(UserAuthInfo user) {
        return UserDTO.builder()
                .email(user.getEmail())
                .id(user.getId())
                .fname("admin")
                .sname("admin")
                .build();
    }
}
