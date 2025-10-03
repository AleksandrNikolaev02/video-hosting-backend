package com.example.business.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("like")
public class Like extends Reaction {
    @Override
    public void setVideo(Video video) {
        super.setVideo(video);
    }

    @Override
    public void setUser(User user) {
        super.setUser(user);
    }
}
