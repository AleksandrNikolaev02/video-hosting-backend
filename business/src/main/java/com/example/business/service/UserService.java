package com.example.business.service;

import com.example.business.mapper.UserMapper;
import com.example.business.model.User;
import com.example.business.repository.ChannelRepository;
import com.example.business.repository.CommentRepository;
import com.example.business.repository.PlaylistRepository;
import com.example.business.repository.ReactionRepository;
import com.example.business.repository.RequestChannelRepository;
import com.example.business.repository.SubscriptionRepository;
import com.example.business.repository.UserRepository;
import com.example.business.repository.VideoRepository;
import com.example.dto.UserDTO;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final TransactionService transactionService;
    private final SubscriptionRepository subscriptionRepository;
    private final ReactionRepository reactionRepository;
    private final CommentRepository commentRepository;
    private final RequestChannelRepository requestChannelRepository;
    private final PlaylistRepository playlistRepository;
    private final ChannelRepository channelRepository;
    private final VideoRepository videoRepository;

    @KafkaListener(topics = "${topics.create-user}",
            groupId = "${kafka.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void recreateUser(UserDTO dto) {
        transactionService.requireTransaction(() -> {
            userRepository.findById(dto.getId()).ifPresent(this::deleteUserCascade);

            User newUser = userMapper.getUserFromUserDTO(dto);
            userRepository.save(newUser);
        });
    }

    private void deleteUserCascade(User user) {
        reactionRepository.deleteByUser(user);
        commentRepository.deleteByCreator(user);
        requestChannelRepository.deleteByCreator(user);
        subscriptionRepository.deleteBySubscriber(user);
        videoRepository.deleteByCreator(user);
        playlistRepository.deleteByOwner(user);
        channelRepository.deleteByAuthor(user);


        userRepository.delete(user);
    }
}
