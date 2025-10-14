package com.example.business.service;

import com.example.business.model.Video;
import com.example.business.model.Viewing;
import com.example.business.util.IpExtractor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class ViewingService {
    private final IpExtractor ipExtractor;
    private final FindEntityService findEntityService;
    private final SaveEntityService saveEntityService;

    public void addViewing(Long userId, String userAgent, UUID filename) {
        String userIp = ipExtractor.getIp();

        Video video = findEntityService.getVideoById(filename);

        log.info("User-Agent: {}, IP: {}", userAgent, userIp);

        Viewing viewing = new Viewing();
        viewing.setVideo(video);

        if (userId != null) {
            viewing.setUserId(userId);
        } else {
            viewing.setUserAgent(userAgent);
            viewing.setIp(userIp);
        }

        video.getViewings().add(viewing);

        saveEntityService.save(video);
    }
}
