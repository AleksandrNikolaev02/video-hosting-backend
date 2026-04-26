package com.example.business.factory;

import com.example.business.model.Comment;
import com.example.business.model.Dislike;
import com.example.business.model.Like;
import com.example.business.model.User;
import com.example.business.model.Video;

public class ReactionFactory {
    public static <T> Like like(T evaluateEntity, User user) {
        Like like = new Like();

        if (evaluateEntity instanceof Video video) {
            like.setVideo(video);
        } else if (evaluateEntity instanceof Comment comment) {
            like.setComment(comment);
        }

        like.setUser(user);
        return like;
    }

    public static <T> Dislike dislike(T evaluateEntity, User user) {
        Dislike dislike = new Dislike();

        if (evaluateEntity instanceof Video video) {
            dislike.setVideo(video);
        } else if (evaluateEntity instanceof Comment comment) {
            dislike.setComment(comment);
        }

        dislike.setUser(user);
        return dislike;
    }
}
