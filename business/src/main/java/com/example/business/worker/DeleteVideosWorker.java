package com.example.business.worker;

import com.example.business.enums.ChannelStatus;
import com.example.business.enums.EvaluateType;
import com.example.business.model.Channel;
import com.example.business.model.Video;
import com.example.business.repository.ChannelRepository;
import com.example.business.repository.PreviewRepository;
import com.example.business.repository.VideoRepository;
import com.example.business.service.cleanup.ReactionCleanupService;
import com.example.business.service.cleanup.ViewingCleanupService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Component
@Slf4j
@AllArgsConstructor
public class DeleteVideosWorker implements Worker {
    private final ChannelRepository channelRepository;
    private final ExecutorService executorService;
    private final ViewingCleanupService viewingCleanupService;
    private final ReactionCleanupService reactionCleanupService;
    private final PreviewRepository previewRepository;
    private final VideoRepository videoRepository;

    @Override
    @Transactional
    public void execute() {
        log.info("Запуск задачи на удаление независимых данных...");

        List<Channel> channels = channelRepository.findByStatus(ChannelStatus.DELETED);

        for (Channel channel : channels) {
            Collection<Video> videos = channel.getVideos();
            videos.forEach(video -> {
                if (!video.getViewings().isEmpty()) {
                    executorService.submit(() -> viewingCleanupService.cleanup(video.getFilename()));
                }

                if (!video.getLikes().isEmpty()) {
                    executorService.submit(() -> reactionCleanupService.cleanup(EvaluateType.LIKE, video.getFilename()));
                }

                if (!video.getDislikes().isEmpty()) {
                    executorService.submit(() -> reactionCleanupService.cleanup(EvaluateType.DISLIKE, video.getFilename()));
                }

                if (video.getPreview() != null) {
                    previewRepository.deletePreview(video.getFilename());
                }

                if (video.getLikes().isEmpty() && video.getDislikes().isEmpty() &&
                        video.getViewings().isEmpty() && video.getPreview() == null) {
                    videoRepository.deleteByChannelId(channel.getId());
                }
            });
        }

        log.info("Окончание асинхронной задачи на удаление независимых данных");
    }
}
