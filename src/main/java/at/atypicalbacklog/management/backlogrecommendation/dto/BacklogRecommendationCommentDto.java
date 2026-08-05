package at.atypicalbacklog.management.backlogrecommendation.dto;

import java.time.Instant;

public record BacklogRecommendationCommentDto(
    String uid,
    Instant creationDate,
    String comment,
    boolean flagged
) {}
