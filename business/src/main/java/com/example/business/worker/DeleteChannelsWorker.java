package com.example.business.worker;

import com.example.business.enums.ChannelStatus;
import com.example.business.model.Channel;
import com.example.business.repository.ChannelRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class DeleteChannelsWorker implements Worker {
    private ChannelRepository channelRepository;

    @Override
    @Transactional
    public void execute() {
        log.info("Удаление осиротевших каналов...");

        List<Channel> channels = channelRepository.findByStatus(ChannelStatus.DELETED);

        channels.forEach(channel -> channelRepository.deleteChannel(channel.getId()));

        log.info("Окончание задачи на удаление каналов...");
    }
}
