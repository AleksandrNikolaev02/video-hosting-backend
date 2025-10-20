package com.example.business.service;

import com.example.business.config.TopicConfig;
import com.example.business.dto.AddVideoInPlaylistDTO;
import com.example.business.dto.CreatePlaylistDTO;
import com.example.business.dto.DeletePlaylistDTO;
import com.example.business.dto.KafkaDeleteChannelDTO;
import com.example.business.enums.ChannelStatus;
import com.example.business.enums.PlaylistStatus;
import com.example.business.factory.PlaylistFactory;
import com.example.business.model.Channel;
import com.example.business.model.Playlist;
import com.example.business.model.User;
import com.example.business.model.Video;
import com.example.business.repository.PlaylistRepository;
import com.example.business.repository.VideoRepository;
import com.example.business.validator.BlockedChannelValidator;
import com.example.business.validator.PermissionValidator;
import com.example.dto.PostMessageDTO;
import com.example.dto.StatusProcessChannel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class PlaylistService {
    private final PlaylistRepository playlistRepository;
    private final FindEntityService findEntityService;
    private final PermissionValidator validator;
    private final VideoRepository videoRepository;
    private final BlockedChannelValidator blockedChannelValidator;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final TopicConfig topicConfig;

    public void createPlaylist(CreatePlaylistDTO dto, Long userId) {
        User user = findEntityService.getUserById(userId);

        blockedChannelValidator.validate(user.getChannel());

        Playlist playlist = PlaylistFactory.create(user, dto.name());

        playlistRepository.save(playlist);
    }

    public void addVideoInPlaylist(AddVideoInPlaylistDTO dto, Long userId) {
        Playlist playlist = findEntityService.getPlaylistById(dto.playlistId());

        blockedChannelValidator.validate(playlist.getChannel());
        validator.validatePlaylistCreator(playlist,userId);

        Video video = findEntityService.getVideoById(dto.filename());

        playlist.getVideos().add(video);
        video.setPlaylist(playlist);

        playlistRepository.save(playlist);
    }

    public List<Playlist> getAllPlaylistsByUser(Long userId, Pageable pageable) {
        User user = findEntityService.getUserById(userId);

        return playlistRepository.findByOwner(user, pageable);
    }

    public List<Video> getAllVideoFromPlaylist(Long userId, Pageable pageable, Long playlistId) {
        Playlist playlist = findEntityService.getPlaylistById(playlistId);

        validator.validatePlaylistCreator(playlist, userId);

        return videoRepository.findByPlaylist(playlist, pageable);
    }

    @Transactional
    public void deletePlaylist(Long userId, DeletePlaylistDTO dto) {
        Playlist playlist = findEntityService.getPlaylistById(dto.playlistId());

        blockedChannelValidator.validate(playlist.getChannel());
        validator.validatePlaylistCreator(playlist, userId);

        deleteLinkFromVideosOfPlaylist(playlist);

        playlistRepository.delete(playlist);
    }

    @KafkaListener(groupId = "${kafka.group-id}",
            topicPartitions = @TopicPartition(
                    topic = "${topics.delete-data-channel}",
                    partitions = "1"
            )
    )
    @Transactional
    public void handleDeleteDataChannel(Object object) {
        StatusProcessChannel status = StatusProcessChannel.DATA_SUCCESS;
        KafkaDeleteChannelDTO dto = (KafkaDeleteChannelDTO) object;
        try {
            Channel channel = findEntityService.getChannelById(dto.channelId());
            channel.getPlaylists().forEach(playlist -> playlist.setStatus(PlaylistStatus.DELETED));
        } catch (Exception e) {
            status = StatusProcessChannel.DATA_FAILURE;

            log.error("Ошибка при удалении видео с канала с id: {}", dto.channelId());

            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        } finally {
            PostMessageDTO message = new PostMessageDTO(status, dto.pipelineKey());
            kafkaTemplate.send(topicConfig.getPublishEventTopic(), message);
        }
    }

    @KafkaListener(groupId = "${kafka.group-id}",
                   topicPartitions = @TopicPartition(
                           topic = "${topics.compensating-transaction-business-service}",
                           partitions = "1"
                   )
    )
    public void compensatingTransactional(Object object) {
        Long userId = (Long) object;

        Channel channel = findEntityService.getUserById(userId).getChannel();
        channel.setStatus(ChannelStatus.ACTIVE);

        channel.getPlaylists().forEach(playlist -> playlist.setStatus(PlaylistStatus.ACTIVE));
    }

    private void deleteLinkFromVideosOfPlaylist(Playlist playlist) {
        playlist.getVideos().forEach(video -> video.setPlaylist(null));
    }
}
