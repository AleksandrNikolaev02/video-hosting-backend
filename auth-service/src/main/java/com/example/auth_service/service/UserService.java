package com.example.auth_service.service;

import com.example.auth_service.dto.ChangeRoleDTO;
import com.example.auth_service.enums.Role;
import com.example.auth_service.exceptions.RoleNotFoundException;
import com.example.auth_service.exceptions.UserNotFoundException;
import com.example.auth_service.model.RoleUser;
import com.example.auth_service.model.UserAuthInfo;
import com.example.auth_service.repository.RoleRepository;
import com.example.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found!"));
    }

    public Optional<UserAuthInfo> loadUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void changeUserRole(ChangeRoleDTO dto) {
        UserAuthInfo user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new UserNotFoundException("User not found!"));

        RoleUser newRole = getRoleFromRoleRepository(dto);

        updateRoleUser(newRole, user);
    }

    private void updateRoleUser(RoleUser newRole, UserAuthInfo user) {
        user.setRole(newRole);
        // saveUserInMicroservice(user);
    }

    private RoleUser getRoleFromRoleRepository(ChangeRoleDTO dto) {
        return roleRepository.findByName(Role.valueOf(dto.roleName().toUpperCase()))
                .orElseThrow(() -> new RoleNotFoundException("Role not found!"));
    }

    public void saveUserInUserRepository(UserAuthInfo user) {
        userRepository.save(user);
    }
}
