package com.example.business.service.generator;

import com.example.business.model.Like;
import com.example.business.model.User;
import com.example.business.model.Video;
import com.example.business.repository.ReactionRepository;
import com.example.business.repository.UserRepository;
import com.example.business.repository.VideoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component(value = "reactionGenerator")
@AllArgsConstructor
public class ReactionGenerator implements Generator {
    private final ReactionRepository reactionRepository;
    private final UserRepository userRepository;
    private final VideoRepository videoRepository;

    @Override
    public void generate(int limit) {
        List<User> users = userRepository.findAll();
        List<Video> videos = videoRepository.findAll();
        List<Like> reactions = new ArrayList<>();

        Random randomizer = new Random();
        for (int i = 0; i < limit; ++i) {
            Like reaction = new Like();
            reaction.setUser(users.get(randomizer.nextInt(0, users.size())));
            reaction.setVideo(videos.get(randomizer.nextInt(0, videos.size())));
            reactions.add(reaction);
        }

        reactionRepository.saveAll(reactions);
    }
}
