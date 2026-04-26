package com.example.business.service;

import com.example.business.dto.ChannelDTO;
import com.example.business.dto.SubscribeDTO;
import com.example.business.dto.UnsubscribeDTO;
import com.example.business.exception.SubscribeAlreadyExistException;
import com.example.business.exception.SubscriptionNotFoundException;
import com.example.business.factory.SubscriptionFactory;
import com.example.business.mapper.ChannelMapper;
import com.example.business.model.Channel;
import com.example.business.model.Subscription;
import com.example.business.model.User;
import com.example.business.repository.ChannelRepository;
import com.example.business.repository.SubscriptionRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SubscriptionService {
    private final FindEntityService findEntityService;
    private final ChannelRepository channelRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ChannelMapper channelMapper;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void subscribe(SubscribeDTO dto, Long userId) {
        User subscriber = findEntityService.getUserById(userId);
        Channel channel = findEntityService.getChannelById(dto.channelId());

        Optional<Subscription> sub = subscriptionRepository.findByChannelAndSubscriber(channel, subscriber);
        if (sub.isPresent()) {
            throw new SubscribeAlreadyExistException("You have already subscribed on this channel!");
        }

        Subscription subscription = SubscriptionFactory.create(subscriber, channel);
        channel.setCountSubs(channel.getCountSubs() + 1);

        subscriptionRepository.save(subscription);
        channelRepository.save(channel);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void unsubscribe(UnsubscribeDTO dto, Long userId) {
        User subscriber = findEntityService.getUserById(userId);
        Channel channel = findEntityService.getChannelById(dto.channelId());

        Optional<Subscription> sub = subscriptionRepository.findByChannelAndSubscriber(channel, subscriber);

        if (sub.isEmpty()) {
            throw new SubscriptionNotFoundException("Subscription not found in this channel!");
        }

        channel.setCountSubs(channel.getCountSubs() - 1);

        subscriptionRepository.delete(sub.get());
    }

    public List<ChannelDTO> getListChannels(Pageable pageable, Long userId) {
        User user = findEntityService.getUserById(userId);

        return subscriptionRepository.findBySubscriber(pageable, user)
                .map(Subscription::getChannel)
                .map(channelMapper::getChannelDtoFromChannel)
                .toList();
    }
}
