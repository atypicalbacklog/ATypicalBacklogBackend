package at.atypicalbacklog.management.backlogrecommendation.dto;

public record CreateBacklogRecommendationResponse(
    String backlogRecommendationUid,
    String gameRecommendationUid
) {}
