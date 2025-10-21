package com.example.business.service;

import com.example.business.config.TopicConfig;
import com.example.business.dto.BelongEvaluateDTO;
import com.example.business.dto.CompensatingTransactionDTO;
import com.example.business.dto.CreateBaseVideoDTO;
import com.example.business.dto.DeleteVideoDTO;
import com.example.business.dto.EvaluateVideoDTO;
import com.example.business.dto.GetEvaluatesVideoDTO;
import com.example.business.dto.KafkaDeleteChannelDTO;
import com.example.business.dto.RequestBelongEvaluateDTO;
import com.example.business.dto.UpdatePathVideoDTO;
import com.example.business.dto.UpdateVideoDTO;
import com.example.business.enums.ChannelStatus;
import com.example.business.enums.VideoStatus;
import com.example.business.exception.UserNotCreateChannelException;
import com.example.business.exception.UserNotFoundException;
import com.example.business.exception.VideoNotFoundException;
import com.example.business.factory.ReactionFactory;
import com.example.business.factory.VideoFactory;
import com.example.business.model.Channel;
import com.example.business.model.Dislike;
import com.example.business.model.Like;
import com.example.business.model.User;
import com.example.business.model.Video;
import com.example.business.repository.ChannelRepository;
import com.example.business.repository.UserRepository;
import com.example.business.repository.VideoRepository;
import com.example.business.validator.BlockedChannelValidator;
import com.example.business.validator.PermissionValidator;
import com.example.dto.PostMessageDTO;
import com.example.dto.StatusProcessChannel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class VideoService {

    private final TopicConfig topicConfig;

    private final VideoRepository videoRepository;
    private final UserRepository userRepository;
    private final FindEntityService findEntityService;
    private final PermissionValidator permissionValidator;
    private final ChannelRepository channelRepository;
    private final BlockedChannelValidator blockedChannelValidator;
    private final KafkaTemplate<String, Object> kafkaTemplate;

//    @KafkaListener(topics = "${topics.create-video}",
//                   groupId = "${kafka.group-id}",
//                   errorHandler = "createVideoHandler")
//    public void createVideo(CreateVideoDTO dto) {
//        User creator = getUserById(dto.getUserId());
//
//        Video video = new Video();
//        video.setPath(dto.getPath());
//        video.setCreator(creator);
//
//        videoRepository.save(video);
//    }

    @Transactional
    public void updateVideo(UpdateVideoDTO dto, UUID filename, Long userId) {
        Video video = findEntityService.getVideoById(filename);

        blockedChannelValidator.validate(video.getChannel());
        permissionValidator.validateCreatorOfVideo(video, userId);

        Optional.ofNullable(dto.description()).ifPresent(video::setDescription);
        Optional.ofNullable(dto.title()).ifPresent(video::setName);
    }

    public Video createVideo(CreateBaseVideoDTO dto, Long userId) {
        User creator = getUserById(userId);

        blockedChannelValidator.validate(creator.getChannel());

        Optional<Channel> channel = channelRepository.findByAuthor(creator);
        if (channel.isEmpty()) {
            throw new UserNotCreateChannelException("User does not create channel yet!");
        }

        Video video = VideoFactory.create(dto, creator);

        videoRepository.save(video);

        return video;
    }

    @Transactional
    public void deleteVideo(DeleteVideoDTO dto, Long userId) {
        Video video = findEntityService.getVideoById(dto.filename());

        blockedChannelValidator.validate(video.getChannel());
        permissionValidator.validateCreatorOfVideo(video, userId);

        videoRepository.delete(video);
    }

    @KafkaListener(groupId = "${kafka.group-id}",
                   topicPartitions = @TopicPartition(
                        topic = "${topics.delete-data-channel}",
                        partitions = "0"
                   ),
            containerFactory = "factoryKafkaDeleteChannelDTO"
    )
    @Transactional
    public void handleDeleteDataChannel(KafkaDeleteChannelDTO dto) {
        StatusProcessChannel status = StatusProcessChannel.DATA_SUCCESS;
        try {
            log.info("Начало выполнения смены статусов у видео...");

            Channel channel = findEntityService.getChannelById(dto.channelId());
            channel.getVideos().forEach(video -> video.setVideoStatus(VideoStatus.DELETED));

            log.info("Смена статусов у видео завершилась успешно!");
        } catch (Exception e) {
            status = StatusProcessChannel.DATA_FAILURE;

            log.error("Ошибка при удалении видео с канала с id: {}", dto.channelId());

            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        } finally {
            PostMessageDTO message = new PostMessageDTO(status, dto.pipelineKey());
            kafkaTemplate.send(topicConfig.getPublishEventTopic(), message);
            log.info("Отправлено сообщение в Camunda");
        }
    }

    @KafkaListener(groupId = "${kafka.group-id}",
            topicPartitions = @TopicPartition(
                    topic = "${topics.compensating-transaction-business-service}",
                    partitions = "0"
            ),
            containerFactory = "factoryCompensatingTransactionDTO"
    )
    public void compensatingTransactional(CompensatingTransactionDTO dto) {
        Channel channel = findEntityService.getUserById(dto.userId()).getChannel();
        channel.setStatus(ChannelStatus.ACTIVE);

        channel.getVideos().forEach(video -> video.setVideoStatus(VideoStatus.UPLOADED));
    }

    public void updateVideoPath(UpdatePathVideoDTO dto, Long userId) {
        Video video = findEntityService.getVideoById(dto.filename());

        blockedChannelValidator.validate(video.getChannel());
        permissionValidator.validateCreatorOfVideo(video, userId);

        // FIXME: а вот тут надо подумать...
        // video.setFilename(dto.getFilename());

        videoRepository.save(video);
    }

    public void postVideo(UUID filename, Long userId) {
        Video video = getVideoByPath(filename);

        blockedChannelValidator.validate(video.getChannel());
        permissionValidator.validateCreatorOfVideo(video, userId);

        video.setVideoStatus(VideoStatus.UPLOADED);
        video.setDate(LocalDateTime.now());

        videoRepository.save(video);
    }

    public Page<Video> getVideos(Long userId, Pageable pageable) {
        return videoRepository.findByCreatorOrderByFilename(userId, pageable);
    }

    @Transactional
    public void evaluateVideo(EvaluateVideoDTO dto, Long userId) {
        Video video = findEntityService.getVideoById(dto.filename());
        User user = getUserById(userId);

        Like currentLike = video.getLikes().stream()
                .filter((like) -> like.getUser().getId().equals(userId))
                .findFirst().orElse(null);

        Dislike currentDislike = video.getDislikes().stream()
                .filter((dislike -> dislike.getUser().getId().equals(userId)))
                .findFirst().orElse(null);

        switch (dto.evaluateType()) {
            case LIKE -> {
                if (currentLike != null) {
                    video.getLikes().remove(currentLike);
                } else {
                    if (currentDislike != null) {
                        video.getDislikes().remove(currentDislike);
                    }

                    addLike(video, user);
                }
            }
            case DISLIKE -> {
                if (currentDislike != null) {
                    video.getDislikes().remove(currentDislike);
                } else {
                    if (currentLike != null) {
                        video.getLikes().remove(currentLike);
                    }

                    addDislike(video, user);
                }
            }
        }
    }

    private void addDislike(Video video, User user) {
        Dislike dislike = ReactionFactory.dislike(video, user);

        video.getDislikes().add(dislike);
    }

    private void addLike(Video video, User user) {
        Like like = ReactionFactory.like(video, user);

        video.getLikes().add(like);
    }

    public GetEvaluatesVideoDTO getEvaluates(UUID filename) {
        findEntityService.getVideoById(filename);

        return videoRepository.getAllEvaluatesByVideo(filename);
    }

    public BelongEvaluateDTO checkBelongEvaluate(RequestBelongEvaluateDTO dto,
                                                 Long userId) {
        Video video = findEntityService.getVideoById(dto.filename());
        BelongEvaluateDTO belongEvaluateDTO = new BelongEvaluateDTO();

        for (Like like : video.getLikes()) {
            if (like.getUser().getId().equals(userId)) {
                belongEvaluateDTO.setLikeBelong(true);
            }
        }

        for (Dislike dislike : video.getDislikes()) {
            if (dislike.getUser().getId().equals(userId)) {
                belongEvaluateDTO.setDislikeBelong(true);
            }
        }

        return belongEvaluateDTO;
    }

    private Video getVideoByPath(UUID filename) {
        return videoRepository.findById(filename).orElseThrow(()
                -> new VideoNotFoundException(String.format("Video with filename %s not found!", filename)));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(()
                -> new UserNotFoundException(String.format("User with id %d not found!", userId)));
    }
}
