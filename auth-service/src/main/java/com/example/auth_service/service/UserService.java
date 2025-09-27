package com.example.auth_service.service;

import com.example.auth_service.config.TopicConfig;
import com.example.auth_service.dto.ChangeRoleDTO;
import com.example.auth_service.enums.Role;
import com.example.auth_service.exceptions.RoleNotFoundException;
import com.example.auth_service.exceptions.UserNotFoundException;
import com.example.auth_service.model.RoleUser;
import com.example.auth_service.model.UserAuthInfo;
import com.example.auth_service.repository.RoleRepository;
import com.example.auth_service.repository.UserRepository;
import com.example.auth_service.util.JsonMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final RoleRepository roleRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final TopicConfig topicConfig;
    private final JsonMapper mapper;
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found!"));
    }

    public void changeUserRole(ChangeRoleDTO dto) {
        UserAuthInfo user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new UserNotFoundException("User not found!"));

        RoleUser newRole = getRoleFromRoleRepository(dto);

        updateRoleUser(newRole, user);
    }

    private void updateRoleUser(RoleUser newRole, UserAuthInfo user) {
        user.setRole(newRole);
        saveUserInMicroservice(user);
    }

    public void saveUserInMicroservice(UserAuthInfo user) {
        kafkaTemplate.send(topicConfig.getSaveUser(), mapper.serialize(user));
    }

    private RoleUser getRoleFromRoleRepository(ChangeRoleDTO dto) {
        return roleRepository.findByName(Role.valueOf(dto.roleName().toUpperCase()))
                .orElseThrow(() -> new RoleNotFoundException("Role not found!"));
    }

    public void saveUserInUserRepository(UserAuthInfo user) {
        userRepository.save(user);
    }
}
