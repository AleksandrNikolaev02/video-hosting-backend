package com.example.business.service;

import com.example.business.mapper.UserMapper;
import com.example.business.repository.UserRepository;
import com.example.dto.UserDTO;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @KafkaListener(topics = "${topics.create-user}",
            groupId = "${kafka.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void createUser(UserDTO dto) {
        userRepository.save(userMapper.getUserFromUserDTO(dto));
    }
}
