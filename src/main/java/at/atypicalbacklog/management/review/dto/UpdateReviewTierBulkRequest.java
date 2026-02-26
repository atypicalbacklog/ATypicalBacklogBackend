package at.atypicalbacklog.management.review.dto;

import java.util.List;

public record UpdateReviewTierBulkRequest(
    List<String> playthroughUids,
    List<String> reviewUids,
    String tierUid
) {}