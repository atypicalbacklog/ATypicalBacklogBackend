package at.atypicalbacklog.management.dto.review.query;

import java.time.LocalDate;

public record PlaythroughWithReviewDto(
    String playthroughUid,
    String playthroughTypeUid,
    String playthroughStateUid,
    LocalDate startedAt,

    String reviewUid,
    LocalDate reviewDate,
    String tierUid,

    boolean hasTextReview
) {}