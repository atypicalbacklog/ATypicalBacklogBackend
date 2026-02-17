package at.atypicalbacklog.management.dto.review.yearly;

public record UpsertYearlyReviewCategoryRequest(
    String uid,
    String yearlyReviewUid,
    String name,
    int order
) {}