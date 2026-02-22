package at.atypicalbacklog.management.dto.review.yearly;

/**
 * Request to update intro/outro text of a yearly review. Null fields are left unchanged.
 */
public record UpdateYearlyReviewRequest(
    String introText,
    String outroText
) {}
