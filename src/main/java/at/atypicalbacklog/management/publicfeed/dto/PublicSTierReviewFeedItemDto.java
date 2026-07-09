package at.atypicalbacklog.management.publicfeed.dto;

import at.atypicalbacklog.management.game.dto.GameOverviewRowDto;

import java.time.LocalDate;

public record PublicSTierReviewFeedItemDto(
    String gameUid,
    String gameInstanceUid,
    String imageUid,
    String name,
    GameOverviewRowDto overview,
    String reviewUid,
    LocalDate reviewDate,
    String tierUid,
    String reviewText,
    String preferredImageUid,
    String presentationType,
    String playthroughUid
) {}
