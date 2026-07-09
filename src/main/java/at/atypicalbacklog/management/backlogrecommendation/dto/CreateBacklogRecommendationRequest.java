package at.atypicalbacklog.management.backlogrecommendation.dto;

public record CreateBacklogRecommendationRequest(
    GameRecommendationInput gameRecommendation,
    BacklogRecommendationInput backlogRecommendation
) {}
