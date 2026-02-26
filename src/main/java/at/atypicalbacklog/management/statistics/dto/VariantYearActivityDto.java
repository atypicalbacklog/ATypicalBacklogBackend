package at.atypicalbacklog.management.statistics.dto;

public record VariantYearActivityDto(
    int year,
    String variantUid,
    long totalSeconds
) {}