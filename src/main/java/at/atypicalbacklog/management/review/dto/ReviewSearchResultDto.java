package at.atypicalbacklog.management.review.dto;

import java.util.List;

public record ReviewSearchResultDto(

    String displayName,
    String thumbnailImageUid,

    List<PlaythroughWithReviewDto> playthroughs
) {}