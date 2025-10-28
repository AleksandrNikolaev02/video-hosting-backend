package com.example.business.util;

import com.example.business.model.Dislike;
import com.example.business.model.Like;

import java.util.Collection;

public interface Evaluatable {
    Collection<Like> getLikes();
    Collection<Dislike> getDislikes();
}
