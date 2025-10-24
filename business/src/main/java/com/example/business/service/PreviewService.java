package com.example.business.service;

import com.example.business.dto.CreateBasePreviewDTO;
import com.example.business.dto.DeletePreviewDTO;
import com.example.business.dto.UpdatePreviewDTO;
import com.example.business.model.Preview;
import com.example.business.model.Video;
import com.example.business.validator.DeleteStatusValidator;
import com.example.business.validator.PermissionValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class PreviewService {
    private final FindEntityService findEntityService;
    private final SaveEntityService saveEntityService;
    private final PermissionValidator validator;
    private final DeleteStatusValidator deleteStatusValidator;

    public void createPreview(CreateBasePreviewDTO dto, Long userId) {
        Video video = findEntityService.getVideoById(dto.filename());

        deleteStatusValidator.validate(video);
        validator.validateCreatorOfVideo(video, userId);

        Preview preview = new Preview();
        preview.setVideo(video);

        video.setPreview(preview);

        saveEntityService.save(video);
    }

    public void updatePreview(UpdatePreviewDTO dto, Long userId) {
        Video video = findEntityService.getVideoById(dto.filename());

        deleteStatusValidator.validate(video);
        validator.validateCreatorOfVideo(video, userId);

        Preview preview = video.getPreview();

        Optional.ofNullable(dto.path()).ifPresent(preview::setPath);

        saveEntityService.save(video);
    }

    @Transactional
    public void deletePreview(DeletePreviewDTO dto, Long userId) {
        Video video = findEntityService.getVideoById(dto.filename());

        deleteStatusValidator.validate(video);
        validator.validateCreatorOfVideo(video, userId);

        video.setPreview(null);

        saveEntityService.save(video);
    }
}
