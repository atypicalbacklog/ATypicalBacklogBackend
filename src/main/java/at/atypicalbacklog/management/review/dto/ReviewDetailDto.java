package at.atypicalbacklog.management.review.dto;

import java.time.LocalDate;
import java.util.List;

public record ReviewDetailDto(
    String reviewUid,
    LocalDate reviewDate,
    String tierUid,
    String text,
    String preferredImageUid,

    LinkedPlaythroughDto linkedPlaythrough,

    String gameInstanceUid,    // game instance of the linked playthrough
    String gameUid,            // game of the linked playthrough's instance
    String displayName,
    String coverImageUid,

    List<LinkedPlaythroughDto> otherPlaythroughs
) {}
