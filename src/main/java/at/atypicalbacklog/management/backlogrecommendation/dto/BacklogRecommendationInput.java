package at.atypicalbacklog.management.backlogrecommendation.dto;

public record BacklogRecommendationInput(
    String gameUid,
    String name,
    String originalRecommendationBy,
    String comment
) {}
