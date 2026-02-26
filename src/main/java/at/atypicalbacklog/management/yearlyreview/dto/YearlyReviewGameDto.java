package at.atypicalbacklog.management.yearlyreview.dto;

public record YearlyReviewGameDto(
    String uid,
    String gameUid,
    String gameInstanceUid,
    String preferredImageUid,
    String text,
    int order
) {}