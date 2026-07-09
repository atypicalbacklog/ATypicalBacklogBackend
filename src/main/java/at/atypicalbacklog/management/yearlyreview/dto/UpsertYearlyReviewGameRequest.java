package at.atypicalbacklog.management.yearlyreview.dto;

public record UpsertYearlyReviewGameRequest(
    String uid,
    String categoryUid,
    String gameUid,
    String gameInstanceUid,
    String preferredImageUid,
    String text,
    int order,
    String presentationType
) {}