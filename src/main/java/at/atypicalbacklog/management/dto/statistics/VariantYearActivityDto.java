package at.atypicalbacklog.management.dto.statistics;

public record VariantYearActivityDto(
    int year,
    String variantUid,
    long totalSeconds
) {}