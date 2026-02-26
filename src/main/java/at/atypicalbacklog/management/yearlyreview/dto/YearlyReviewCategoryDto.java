package at.atypicalbacklog.management.yearlyreview.dto;

import java.util.List;

public record YearlyReviewCategoryDto(
    String uid,
    String name,
    int order,
    List<YearlyReviewGameDto> games
) {}