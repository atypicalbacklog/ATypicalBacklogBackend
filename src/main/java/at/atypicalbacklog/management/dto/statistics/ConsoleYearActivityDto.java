package at.atypicalbacklog.management.dto.statistics;

public record ConsoleYearActivityDto(
    int year,
    String consoleUid,
    long totalSeconds,
    int distinctGames,
    int bookingCount
) {}