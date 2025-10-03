package com.example.business.factory;

import com.example.business.model.Dislike;
import com.example.business.model.Like;
import com.example.business.model.User;
import com.example.business.model.Video;

public class ReactionFactory {
    public static Like like(Video video, User user) {
        Like like = new Like();

        like.setVideo(video);
        like.setUser(user);
        return like;
    }

    public static Dislike dislike(Video video, User user) {
        Dislike dislike = new Dislike();

        dislike.setUser(user);
        dislike.setVideo(video);

        return dislike;
    }
}
