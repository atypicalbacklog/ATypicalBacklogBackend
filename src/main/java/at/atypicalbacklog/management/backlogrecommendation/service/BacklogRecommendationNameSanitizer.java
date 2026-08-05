package at.atypicalbacklog.management.backlogrecommendation.service;

import at.atypicalbacklog.management.backlogrecommendation.domain.BacklogRecommendationState;
import at.atypicalbacklog.management.backlogrecommendation.dto.GameRecommendationDto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BacklogRecommendationNameSanitizer {

    private final String awaitingReviewName;

    public BacklogRecommendationNameSanitizer(
        @Value("${app.backlog-recommendation.awaiting-review-name:Game awaiting Review}")
        String awaitingReviewName
    ) {
        this.awaitingReviewName = awaitingReviewName;
    }

    public String awaitingReviewName() {
        return awaitingReviewName;
    }

    public boolean shouldSanitize(BacklogRecommendationState state, String gameUid, boolean hasLinkedGame) {
        if (state != BacklogRecommendationState.NEW) {
            return false;
        }
        return gameUid == null || gameUid.isBlank() || !hasLinkedGame;
    }

    public String sanitizeName(String name, boolean sanitize) {
        return sanitize ? awaitingReviewName : name;
    }

    public GameRecommendationDto sanitizeGameRecommendation(GameRecommendationDto gameRecommendation, boolean sanitize) {
        if (!sanitize || gameRecommendation == null) {
            return gameRecommendation;
        }
        return new GameRecommendationDto(
            gameRecommendation.uid(),
            gameRecommendation.creationDate(),
            awaitingReviewName,
            gameRecommendation.releaseDate(),
            gameRecommendation.externalIgdbId(),
            gameRecommendation.externalIgdbIdImageURL()
        );
    }

    /** Owner comment on the recommendation: hidden in DTOs while state is NEW. */
    public String sanitizeOwnerComment(BacklogRecommendationState state, String comment) {
        return state == BacklogRecommendationState.NEW ? null : comment;
    }
}
