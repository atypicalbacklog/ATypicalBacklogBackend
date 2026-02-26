package at.atypicalbacklog.management.yearlyreview.dto;

import java.util.List;

public record YearlyReviewDto(
    String uid,
    int year,
    boolean valid,
    String introText,
    String outroText,
    List<YearlyReviewCategoryDto> categories
) {}