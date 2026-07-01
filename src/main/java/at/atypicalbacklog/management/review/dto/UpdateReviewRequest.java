package at.atypicalbacklog.management.review.dto;

import java.time.LocalDate;

public record UpdateReviewRequest(
    String reviewUid,
    LocalDate reviewDate,
    String tierUid,
    String text,
    String preferredImageUid
) {}