package at.atypicalbacklog.management.dto.review.command;

import java.util.List;

public record UpdateReviewTierBulkRequest(
    List<String> playthroughUids,
    List<String> reviewUids,
    String tierUid
) {}