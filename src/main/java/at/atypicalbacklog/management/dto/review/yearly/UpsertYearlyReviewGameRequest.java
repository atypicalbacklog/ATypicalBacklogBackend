package at.atypicalbacklog.management.dto.review.yearly;

public record UpsertYearlyReviewGameRequest(
    String uid,
    String categoryUid,
    String gameUid,
    String gameInstanceUid,
    String preferredImageUid,
    String text,
    int order
) {}