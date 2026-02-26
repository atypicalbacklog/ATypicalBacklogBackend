package at.atypicalbacklog.management.review.dto;

import java.time.LocalDate;
import java.util.List;

public record ReviewDetailDto(
    String reviewUid,
    LocalDate reviewDate,
    String tierUid,
    String text,

    LinkedPlaythroughDto linkedPlaythrough,

    String displayName,
    String coverImageUid,

    List<LinkedPlaythroughDto> otherPlaythroughs
) {}
