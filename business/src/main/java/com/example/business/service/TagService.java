package com.example.business.service;

import com.example.business.dto.TagDTO;
import com.example.business.model.Tag;
import com.example.business.model.Video;
import com.example.business.repository.TagRepository;
import com.example.business.repository.VideoRepository;
import com.example.business.validator.PermissionValidator;
import lombok.AllArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class TagService {
    private final FindEntityService findEntityService;
    private final PermissionValidator permissionValidator;
    private final VideoRepository videoRepository;
    private final TagRepository tagRepository;

    @Transactional
    public void addTags(TagDTO dto, Long userId) {
        Video video = findEntityService.getVideoById(dto.filename());

        permissionValidator.validateCreatorOfVideo(video, userId);

        Set<Tag> videoTags = video.getTags();

        Set<String> uniqueNames = new LinkedHashSet<>();
        for (String name : dto.names()) {
            if (name != null && !name.isBlank()) {
                uniqueNames.add(name.trim());
            }
        }

        for (String name : uniqueNames) {
            videoTags.add(getOrCreateTag(name));
        }

        video.setTags(videoTags);

        videoRepository.save(video);
    }

    @Transactional
    public void deleteTags(TagDTO dto, Long userId) {
        Video video = findEntityService.getVideoById(dto.filename());

        permissionValidator.validateCreatorOfVideo(video, userId);

        video.getTags().removeIf(tag -> dto.names().contains(tag.getName()));

        videoRepository.save(video);
    }

    private Tag getOrCreateTag(String name) {
        Optional<Tag> existingTag = tagRepository.findByName(name);
        if (existingTag.isPresent()) {
            return existingTag.get();
        }

        try {
            return tagRepository.saveAndFlush(new Tag(name));
        } catch (DataIntegrityViolationException e) {
            return tagRepository.findByName(name)
                    .orElseThrow(() -> e);
        }
    }
}
