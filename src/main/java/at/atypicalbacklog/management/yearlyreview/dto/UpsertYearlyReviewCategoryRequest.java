package at.atypicalbacklog.management.yearlyreview.dto;

public record UpsertYearlyReviewCategoryRequest(
    String uid,
    String yearlyReviewUid,
    String name,
    int order
) {}