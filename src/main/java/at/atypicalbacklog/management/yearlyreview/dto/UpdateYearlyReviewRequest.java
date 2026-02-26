package at.atypicalbacklog.management.yearlyreview.dto;

/**
 * Request to update intro/outro text of a yearly review. Null fields are left unchanged.
 */
public record UpdateYearlyReviewRequest(
    String introText,
    String outroText
) {}
