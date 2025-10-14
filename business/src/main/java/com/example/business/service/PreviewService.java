package com.example.business.service;

import com.example.business.dto.CreateBasePreviewDTO;
import com.example.business.dto.DeletePreviewDTO;
import com.example.business.dto.UpdatePreviewDTO;
import com.example.business.model.Preview;
import com.example.business.model.Video;
import dev.alex.auth.starter.auth_spring_boot_starter.exception.NoRightsException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class PreviewService {
    private final FindEntityService findEntityService;
    private final SaveEntityService saveEntityService;

    public void createPreview(CreateBasePreviewDTO dto, Long userId) {
        Video video = findEntityService.getVideoById(dto.filename());

        validatePermissions(video, userId);

        Preview preview = new Preview();
        preview.setVideo(video);

        video.setPreview(preview);

        saveEntityService.save(video);
    }

    public void updatePreview(UpdatePreviewDTO dto, Long userId) {
        Video video = findEntityService.getVideoById(dto.filename());

        validatePermissions(video, userId);

        Preview preview = video.getPreview();

        Optional.ofNullable(dto.path()).ifPresent(preview::setPath);

        saveEntityService.save(video);
    }

    @Transactional
    public void deletePreview(DeletePreviewDTO dto, Long userId) {
        Video video = findEntityService.getVideoById(dto.filename());

        validatePermissions(video, userId);

        video.setPreview(null);

        saveEntityService.save(video);
    }

    public void validatePermissions(Video video, Long userId) {
        if (!video.getCreator().getId().equals(userId)) {
            throw new NoRightsException("You aren't creator of video!");
        }
    }
}
