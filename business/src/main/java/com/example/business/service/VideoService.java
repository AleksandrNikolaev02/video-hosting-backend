package com.example.business.service;

import com.example.business.dto.BelongEvaluateDTO;
import com.example.business.dto.CreateBaseVideoDTO;
import com.example.business.dto.EvaluateVideoDTO;
import com.example.business.dto.GetEvaluatesVideoDTO;
import com.example.business.dto.RequestBelongEvaluateDTO;
import com.example.business.dto.UpdatePathVideoDTO;
import com.example.business.dto.UpdateVideoDTO;
import com.example.business.enums.EvaluateType;
import com.example.business.enums.VideoStatus;
import com.example.business.exception.UserNotFoundException;
import com.example.business.exception.VideoAlreadyEvaluateException;
import com.example.business.exception.VideoNotFoundException;
import com.example.business.factory.ReactionFactory;
import com.example.business.model.Dislike;
import com.example.business.model.Like;
import com.example.business.model.Reaction;
import com.example.business.model.User;
import com.example.business.model.Video;
import com.example.business.repository.UserRepository;
import com.example.business.repository.VideoRepository;
import com.example.dto.CreateVideoDTO;
import dev.alex.auth.starter.auth_spring_boot_starter.exception.NoRightsException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@Service
@AllArgsConstructor
public class VideoService {
    private final VideoRepository videoRepository;
    private final UserRepository userRepository;

    @KafkaListener(topics = "${topics.create-video}",
                   groupId = "${kafka.group-id}",
                   errorHandler = "createVideoHandler")
    public void createVideo(CreateVideoDTO dto) {
        User creator = getUserById(dto.getUserId());

        Video video = new Video();
        video.setPath(dto.getPath());
        video.setCreator(creator);

        videoRepository.save(video);
    }

    public void updateVideo(UpdateVideoDTO dto, String path, Long userId) {
        Video video = getVideoByPath(path);

        validateCreatorOfVideo(video, userId);

        Optional.ofNullable(dto.description()).ifPresent(video::setDescription);
        Optional.ofNullable(dto.title()).ifPresent(video::setName);

        videoRepository.save(video);
    }

    public Video createVideo(CreateBaseVideoDTO dto, Long userId) {
        User creator = getUserById(userId);

        Video video = new Video();
        video.setDescription(dto.getDescription());
        video.setName(dto.getTitle());
        video.setCreator(creator);
        video.setVideoStatus(VideoStatus.DRAFT);

        return videoRepository.save(video);
    }

    public void updateVideoPath(UpdatePathVideoDTO dto, Long userId) {
        Video video = getVideoById(dto.getVideoId());

        validateCreatorOfVideo(video, userId);

        video.setPath(dto.getPath());

        videoRepository.save(video);
    }

    public void postVideo(String filename, Long userId) {
        Video video = getVideoByPath(filename);

        validateCreatorOfVideo(video, userId);

        video.setVideoStatus(VideoStatus.UPLOADED);
        video.setDate(LocalDateTime.now());

        videoRepository.save(video);
    }

    public Page<Video> getVideos(Long userId, Pageable pageable) {
        return videoRepository.findAllVideoByUserId(userId, pageable);
    }

    @Transactional
    public void evaluateVideo(EvaluateVideoDTO dto, Long userId) {
        Video video = getVideoById(dto.getVideoId());
        User user = getUserById(userId);

        Like currentLike = video.getLikes().stream()
                .filter((like) -> like.getUser().getId().equals(userId))
                .findFirst().orElse(null);

        Dislike currentDislike = video.getDislikes().stream()
                .filter((dislike -> dislike.getUser().getId().equals(userId)))
                .findFirst().orElse(null);

        switch (dto.getEvaluateType()) {
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

    public GetEvaluatesVideoDTO getEvaluates(Long videoId) {
        return videoRepository.getAllEvaluatesByVideo(videoId);
    }

    public BelongEvaluateDTO checkBelongEvaluate(RequestBelongEvaluateDTO dto,
                                                 Long userId) {
        Video video = getVideoById(dto.getVideoId());
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

    private void validateAlreadyEvaluation(Collection<? extends Reaction> reactions,
                                           Long videoId, Long userId) {
        for (Reaction reaction : reactions) {
            if (reaction.getUser().getId().equals(userId)) {
                throw new VideoAlreadyEvaluateException(
                        String.format("Video with id %s has already evaluated!", videoId));
            }
        }
    }

    private Reaction createReaction(EvaluateType type, User user, Video video) {
        return switch (type) {
            case LIKE -> ReactionFactory.like(video, user);
            case DISLIKE -> ReactionFactory.dislike(video, user);
        };
    }

    private void validateCreatorOfVideo(Video video, Long userId) {
        if (!video.getCreator().getId().equals(userId)) {
            throw new NoRightsException("You are not creator of video!");
        }
    }

    private Video getVideoById(Long videoId) {
        return videoRepository.findById(videoId).orElseThrow(()
                -> new VideoNotFoundException(String.format("Video with id %d not found!", videoId)));
    }

    private Video getVideoByPath(String filename) {
        return videoRepository.findVideoByPath(filename).orElseThrow(()
                -> new VideoNotFoundException(String.format("Video with filename %s not found!", filename)));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(()
                -> new UserNotFoundException(String.format("User with id %d not found!", userId)));
    }
}
