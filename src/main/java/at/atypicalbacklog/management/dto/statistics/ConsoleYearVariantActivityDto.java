package at.atypicalbacklog.management.dto.statistics;

public record ConsoleYearVariantActivityDto(
    int year,
    String variantUid,
    String consoleUid,
    long totalSeconds,
    int distinctGames,
    int bookingCount
) {}