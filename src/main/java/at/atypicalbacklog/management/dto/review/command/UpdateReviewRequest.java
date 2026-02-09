package at.atypicalbacklog.management.dto.review.command;

import java.time.LocalDate;

public record UpdateReviewRequest(
    String reviewUid,
    LocalDate reviewDate,
    String tierUid,
    String text
) {}