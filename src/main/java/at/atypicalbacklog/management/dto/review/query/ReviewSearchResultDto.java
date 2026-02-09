package at.atypicalbacklog.management.dto.review.query;

import java.util.List;

public record ReviewSearchResultDto(

    String displayName,
    String thumbnailImageUid,

    List<PlaythroughWithReviewDto> playthroughs
) {}