package com.example.business.service;

import com.example.business.config.TopicConfig;
import com.example.business.dto.BlockedChannelDTO;
import com.example.business.dto.ChangeOwnerDTO;
import com.example.business.dto.CreateChannelDTO;
import com.example.business.dto.KafkaDeleteChannelDTO;
import com.example.business.dto.SendRequestDTO;
import com.example.business.dto.UpdateChannelDTO;
import com.example.business.enums.ChannelStatus;
import com.example.business.exception.ChannelAlreadyExistsException;
import com.example.business.factory.BlockedChannelFactory;
import com.example.business.factory.ChannelFactory;
import com.example.business.factory.PostMessageFactory;
import com.example.business.factory.RequestChannelFactory;
import com.example.business.model.BlockedChannel;
import com.example.business.model.Channel;
import com.example.business.model.RequestChannel;
import com.example.business.model.User;
import com.example.business.repository.BlockedChannelRepository;
import com.example.business.repository.ChannelRepository;
import com.example.business.repository.RequestChannelRepository;
import com.example.business.validator.BlockedChannelValidator;
import com.example.business.validator.DeleteStatusValidator;
import com.example.business.validator.PermissionValidator;
import com.example.business.worker.DeleteChannelsWorker;
import com.example.business.worker.DeletePlaylistsWorker;
import com.example.business.worker.DeleteVideosWorker;
import com.example.business.worker.UnlockExpiresChannelsWorker;
import com.example.dto.PostMessageDTO;
import dev.alex.auth.starter.auth_spring_boot_starter.exception.NoRightsException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@AllArgsConstructor
public class ChannelService {
    private final ChannelRepository channelRepository;
    private final FindEntityService findEntityService;
    private final PermissionValidator permissionValidator;
    private final RequestChannelRepository requestChannelRepository;
    private final BlockedChannelRepository blockedChannelRepository;
    private final BlockedChannelValidator blockedChannelValidator;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final TopicConfig topicConfig;
    private final ExecutorService executorService;
    private final UnlockExpiresChannelsWorker unlockExpiresChannelsWorker;
    private final DeletePlaylistsWorker deletePlaylistsWorker;
    private final DeleteVideosWorker deleteVideosWorker;
    private final DeleteChannelsWorker deleteChannelsWorker;
    private final DeleteStatusValidator deleteStatusValidator;

    public void createChannel(CreateChannelDTO dto, Long userId) {
        User authorChannel = findEntityService.getUserById(userId);

        validateChannelAlreadyExists(authorChannel.getChannel());

        Channel channel = ChannelFactory.create(authorChannel, dto);

        channelRepository.save(channel);
    }

    public void updateDataChannel(UpdateChannelDTO dto, Long userId) {
        User author = findEntityService.getUserById(userId);

        Channel channel = channelRepository.findByAuthor(author)
                .orElseThrow(() -> new NoRightsException("You are not creator channel!"));

        deleteStatusValidator.validate(channel);

        blockedChannelValidator.validate(channel);
        permissionValidator.validateChannelCreator(channel, userId);

        Optional.ofNullable(dto.description()).ifPresent(channel::setDescription);
        Optional.ofNullable(dto.name()).ifPresent(channel::setName);

        channelRepository.save(channel);
    }

    @Transactional
    public void deleteChannel(Long userId, String pipelineKey) {
        try {
            User author = findEntityService.getUserById(userId);

            Channel channel = channelRepository.findByAuthor(author)
                    .orElseThrow(() -> new NoRightsException("You are not creator channel!"));

            blockedChannelValidator.validate(channel);

            channel.setStatus(ChannelStatus.DELETED);

            KafkaDeleteChannelDTO dto = new KafkaDeleteChannelDTO(channel.getId(), pipelineKey);

            kafkaTemplate.send(topicConfig.getDeleteDataChannel(), 0, "", dto); // событие на удаление видео
            kafkaTemplate.send(topicConfig.getDeleteDataChannel(), 1, "", dto); // событие на удаление плейлистов
        } catch (Exception exception) {
            log.error("Ошибка при удалении канана пользователя с id: {}", userId);

            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            PostMessageDTO dto = PostMessageFactory.createFailureDto(pipelineKey);

            kafkaTemplate.send(topicConfig.getPublishEventTopic(), dto);
        }
    }

    @Transactional
    public void dropChannel(Long channelId) {
        Channel channel = findEntityService.getChannelById(channelId);

        deleteStatusValidator.validate(channel);

        channelRepository.delete(channel);
    }

    @Transactional
    public void changeOwnerChannel(ChangeOwnerDTO dto) {
        Channel channel = findEntityService.getChannelById(dto.channelId());

        deleteStatusValidator.validate(channel);

        User newOwner = findEntityService.getUserById(dto.newOwnerId());

        channel.setAuthor(newOwner);
    }

    public void sendRequest(SendRequestDTO dto, Long userId) {
        User user = findEntityService.getUserById(userId);

        RequestChannel requestChannel = RequestChannelFactory.create(user, dto.status());

        requestChannelRepository.save(requestChannel);
    }

    @Transactional
    public void removeRequest(Long userId, Long requestId) {
        RequestChannel requestChannel = findEntityService.getRequestChannelById(requestId);

        permissionValidator.validateRequestChannelCreator(requestChannel, userId);

        requestChannelRepository.delete(requestChannel);
    }

    public List<RequestChannel> getUserRequests(Long userId) {
        User user = findEntityService.getUserById(userId);

        return user.getRequests();
    }

    public Page<RequestChannel> getAllRequests(Pageable pageable) {
        return requestChannelRepository.findAll(pageable);
    }

    public void blockVideo(BlockedChannelDTO dto) {
        Channel channel = findEntityService.getChannelById(dto.channelId());

        deleteStatusValidator.validate(channel);

        BlockedChannel blockedChannel = BlockedChannelFactory.create(dto, channel);

        blockedChannelRepository.save(blockedChannel);
    }

    public Page<BlockedChannel> getAllBlockedChannels(Pageable pageable) {
        return blockedChannelRepository.findAll(pageable);
    }

    @Transactional
    public void unblockChannel(Long channelId) {
        BlockedChannel blockedChannel = findEntityService.getBlockedChannelById(channelId);

        blockedChannelRepository.delete(blockedChannel);
    }

    @Scheduled(fixedDelayString = "${schedule.time-expired-channel}", timeUnit = TimeUnit.SECONDS)
    public void unblockExpiresChannels() {
        executorService.submit(unlockExpiresChannelsWorker::execute);
    }

    @Scheduled(fixedDelayString = "${schedule.time-delete-videos}", timeUnit = TimeUnit.SECONDS)
    public void deleteMarkedVideos() {
        executorService.submit(deleteVideosWorker::execute);
    }

    @Scheduled(fixedDelayString = "${schedule.time-delete-playlist}", timeUnit = TimeUnit.SECONDS)
    public void deleteMarkedPlaylist() {
        executorService.submit(deletePlaylistsWorker::execute);
    }

    @Scheduled(fixedDelayString = "${schedule.time-delete-channel}", timeUnit = TimeUnit.SECONDS)
    public void deleteMarkedChannels() {
        executorService.submit(deleteChannelsWorker::execute);
    }

    private void validateChannelAlreadyExists(Channel channel) {
        if (channel != null) {
            throw new ChannelAlreadyExistsException("You already have a channel!");
        }
    }
}
