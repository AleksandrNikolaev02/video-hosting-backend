package com.example.business.service;

import com.example.business.dto.CreateBaseVideoDTO;
import com.example.business.dto.UpdatePathVideoDTO;
import com.example.business.dto.UpdateVideoDTO;
import com.example.business.enums.VideoStatus;
import com.example.business.exception.UserNotFoundException;
import com.example.business.exception.VideoNotFoundException;
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

import java.time.LocalDateTime;
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
        User creator = userRepository.findById(dto.getUserId()).orElseThrow(()
                -> new UserNotFoundException("User not found!"));

        Video video = new Video();
        video.setPath(dto.getPath());
        video.setCreator(creator);

        videoRepository.save(video);
    }

    public void updateVideo(UpdateVideoDTO dto, String path, Long userId) {
        Video video = videoRepository.findVideoByPath(path).orElseThrow(()
                -> new VideoNotFoundException("Video not found!"));

        validateCreatorOfVideo(video, userId);

        Optional.ofNullable(dto.description()).ifPresent(video::setDescription);
        Optional.ofNullable(dto.title()).ifPresent(video::setName);

        videoRepository.save(video);
    }

    public Video createVideo(CreateBaseVideoDTO dto, Long userId) {
        User creator = userRepository.findById(userId).orElseThrow(()
                -> new UserNotFoundException("User not found!"));

        Video video = new Video();
        video.setDescription(dto.getDescription());
        video.setName(dto.getTitle());
        video.setCreator(creator);
        video.setVideoStatus(VideoStatus.DRAFT);

        return videoRepository.save(video);
    }

    public void updateVideoPath(UpdatePathVideoDTO dto, Long userId) {
        Video video = videoRepository.findById(dto.getVideoId()).orElseThrow(()
                -> new VideoNotFoundException(String.format("Video with id %d not found!", dto.getVideoId())));

        validateCreatorOfVideo(video, userId);

        video.setPath(dto.getPath());

        videoRepository.save(video);
    }

    public void postVideo(String filename, Long userId) {
        Video video = videoRepository.findVideoByPath(filename).orElseThrow(()
                -> new VideoNotFoundException(String.format("Video with filename %s not found!", filename)));

        validateCreatorOfVideo(video, userId);

        video.setVideoStatus(VideoStatus.UPLOADED);
        video.setDate(LocalDateTime.now());

        videoRepository.save(video);
    }

    public Page<Video> getVideos(Long userId, Pageable pageable) {
        return videoRepository.findAllVideoByUserId(userId, pageable);
    }

    private void validateCreatorOfVideo(Video video, Long userId) {
        if (!video.getCreator().getId().equals(userId)) {
            throw new NoRightsException("You are not creator of video!");
        }
    }
}
