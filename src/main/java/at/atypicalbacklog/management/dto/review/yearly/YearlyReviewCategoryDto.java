package at.atypicalbacklog.management.dto.review.yearly;

import java.util.List;

public record YearlyReviewCategoryDto(
    String uid,
    String name,
    int order,
    List<YearlyReviewGameDto> games
) {}