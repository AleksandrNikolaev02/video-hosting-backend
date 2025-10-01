package com.example.business.service;

import com.example.business.dto.CreateBaseVideoDTO;
import com.example.business.dto.UpdateVideoDTO;
import com.example.business.enums.VideoStatus;
import com.example.business.exception.UserNotFoundException;
import com.example.business.exception.VideoNotFoundException;
import com.example.business.model.User;
import com.example.business.model.Video;
import com.example.business.repository.UserRepository;
import com.example.business.repository.VideoRepository;
import com.example.dto.CreateVideoDTO;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

    public void updateVideo(UpdateVideoDTO dto, String filename) {
        Video video = videoRepository.findVideoByPath(filename).orElseThrow(()
                -> new VideoNotFoundException("Video not found!"));

        video.setName(dto.title());
        video.setDescription(dto.description());

        videoRepository.save(video);
    }

    public void createVideo(CreateBaseVideoDTO dto, Long userId) {
        User creator = userRepository.findById(userId).orElseThrow(()
                -> new UserNotFoundException("User not found!"));

        Video video = new Video();
        video.setDescription(dto.getDescription());
        video.setName(dto.getTitle());
        video.setCreator(creator);
        video.setVideoStatus(VideoStatus.DRAFT);

        videoRepository.save(video);
    }

    public void postVideo(String filename) {
        Video video = videoRepository.findVideoByPath(filename).orElseThrow(()
                -> new VideoNotFoundException(String.format("Video with filename %s not found!", filename)));

        video.setVideoStatus(VideoStatus.UPLOADED);
        video.setDate(LocalDateTime.now());
        video.setPath(filename);

        videoRepository.save(video);
    }

    public Page<Video> getVideos(Long userId, Pageable pageable) {
        return videoRepository.findAllVideoByUserId(userId, pageable);
    }
}
