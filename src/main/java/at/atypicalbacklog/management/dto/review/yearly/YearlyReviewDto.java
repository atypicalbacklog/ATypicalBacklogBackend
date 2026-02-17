package at.atypicalbacklog.management.dto.review.yearly;

import java.util.List;

public record YearlyReviewDto(
    String uid,
    int year,
    boolean valid,
    List<YearlyReviewCategoryDto> categories
) {}