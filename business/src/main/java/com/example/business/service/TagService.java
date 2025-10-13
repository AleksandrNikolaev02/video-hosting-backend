package com.example.business.service;

import com.example.business.dto.TagDTO;
import com.example.business.mapper.TagMapper;
import com.example.business.model.Tag;
import com.example.business.model.Video;
import com.example.business.repository.TagRepository;
import com.example.business.repository.VideoRepository;
import com.example.business.validator.PermissionValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class TagService {
    private final FindEntityService findEntityService;
    private final PermissionValidator permissionValidator;
    private final VideoRepository videoRepository;
    private final TagRepository tagRepository;

    public void addTags(TagDTO dto, Long userId) {
        Video video = findEntityService.getVideoById(dto.videoId());

        permissionValidator.validateCreatorOfVideo(video, userId);

        List<Tag> tags = dto.names()
                .stream()
                .map(name -> tagRepository.findByName(name)
                        .orElseGet(() -> tagRepository.save(new Tag(name))))
                .toList();

        video.getTags().addAll(tags);

        videoRepository.save(video);
    }

    @Transactional
    public void deleteTags(TagDTO dto, Long userId) {
        Video video = findEntityService.getVideoById(dto.videoId());

        permissionValidator.validateCreatorOfVideo(video, userId);

        video.getTags().removeIf(tag -> dto.names().contains(tag.getName()));

        videoRepository.save(video);
    }
}
