package com.example.business.service;

import com.example.business.enums.EvaluateType;
import com.example.business.factory.ReactionFactory;
import com.example.business.model.Dislike;
import com.example.business.model.Like;
import com.example.business.model.User;
import com.example.business.util.Evaluatable;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@AllArgsConstructor
public class EvaluateService {
    private final FindEntityService findEntityService;

    @Transactional
    public <T extends Evaluatable> void evaluate(EvaluateType evaluateType,
                                                 Long userId, T evaluateEntity) {
        User user = findEntityService.getUserById(userId);

        Like currentLike = evaluateEntity.getLikes().stream()
                .filter((like) -> like.getUser().getId().equals(userId))
                .findFirst().orElse(null);

        Dislike currentDislike = evaluateEntity.getDislikes().stream()
                .filter((dislike -> dislike.getUser().getId().equals(userId)))
                .findFirst().orElse(null);

        switch (evaluateType) {
            case LIKE -> {
                if (currentLike != null) {
                    evaluateEntity.getLikes().remove(currentLike);
                } else {
                    if (currentDislike != null) {
                        evaluateEntity.getDislikes().remove(currentDislike);
                    }

                    addLike(evaluateEntity, user);
                }
            }
            case DISLIKE -> {
                if (currentDislike != null) {
                    evaluateEntity.getDislikes().remove(currentDislike);
                } else {
                    if (currentLike != null) {
                        evaluateEntity.getLikes().remove(currentLike);
                    }

                    addDislike(evaluateEntity, user);
                }
            }
        }
    }

    private <T extends Evaluatable> void addLike(T evaluateEntity, User user) {
        Like like = ReactionFactory.like(evaluateEntity, user);

        evaluateEntity.getLikes().add(like);
    }

    private <T extends Evaluatable> void addDislike(T evaluateEntity, User user) {
        Dislike dislike = ReactionFactory.dislike(evaluateEntity, user);

        evaluateEntity.getDislikes().add(dislike);
    }
}
