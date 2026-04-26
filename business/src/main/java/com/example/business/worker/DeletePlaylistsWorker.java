package com.example.business.worker;

import com.example.business.enums.ChannelStatus;
import com.example.business.model.Channel;
import com.example.business.repository.ChannelRepository;
import com.example.business.repository.PlaylistRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class DeletePlaylistsWorker implements Worker {
    private final PlaylistRepository playlistRepository;
    private final ChannelRepository channelRepository;

    @Override
    @Transactional
    public void execute() {
        log.info("Удаление осиротевших плейлистов...");

        List<Channel> channels = channelRepository.findByStatus(ChannelStatus.DELETED);
        for (Channel channel : channels) {
            playlistRepository.deleteEmptyPlaylistsByChannelId(channel.getId());
        }

        log.info("Окончание задачи на удаление осиротевших плейлистов...");
    }
}
