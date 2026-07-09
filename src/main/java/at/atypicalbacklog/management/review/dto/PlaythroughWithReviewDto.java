package at.atypicalbacklog.management.review.dto;

import at.atypicalbacklog.management.completion.dto.GameCompletionInfoDto;

import java.time.LocalDate;
import java.util.List;

public record PlaythroughWithReviewDto(
    String playthroughUid,
    String playthroughTypeUid,
    String playthroughStateUid,
    LocalDate startedAt,

    String gameInstanceUid,
    String inputUid,
    long totalPlaytimeSeconds,
    String coverImageUid,        // game instance cover, or game cover when the instance has none

    String reviewUid,
    LocalDate reviewDate,
    String tierUid,
    String preferredImageUid,    // review's preferred image; null when no review or not set
    String presentationType,     // null when no review

    boolean hasTextReview,

    List<LinkedDlcDto> linkedDlcs,              // DLCs linked to this playthrough
    List<GameCompletionInfoDto> completions     // game instance completion + linked DLC completions
) {}
