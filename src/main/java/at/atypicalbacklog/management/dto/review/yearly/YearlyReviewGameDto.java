package at.atypicalbacklog.management.dto.review.yearly;

public record YearlyReviewGameDto(
    String uid,
    String gameUid,
    String gameInstanceUid,
    String preferredImageUid,
    String text,
    int order
) {}