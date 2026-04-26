package com.example.business.service.generator;

import com.example.business.model.Channel;
import com.example.business.model.User;
import com.example.business.repository.ChannelRepository;
import com.example.business.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("swapper")
@AllArgsConstructor
public class Swapper implements Swappable {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public void swap() {
        List<Channel> channels = channelRepository.findAll();
        List<User> users = userRepository.findAll();

        for (int i = 0; i < channels.size(); ++i) {
            channels.get(i).setAuthor(users.get(i));
        }

        channelRepository.saveAll(channels);
    }
}
