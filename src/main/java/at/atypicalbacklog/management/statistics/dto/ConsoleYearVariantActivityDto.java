package at.atypicalbacklog.management.statistics.dto;

public record ConsoleYearVariantActivityDto(
    int year,
    String variantUid,
    String consoleUid,
    long totalSeconds,
    int distinctGames,
    int bookingCount
) {}