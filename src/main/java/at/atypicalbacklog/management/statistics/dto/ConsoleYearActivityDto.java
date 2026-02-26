package at.atypicalbacklog.management.statistics.dto;

public record ConsoleYearActivityDto(
    int year,
    String consoleUid,
    long totalSeconds,
    int distinctGames,
    int bookingCount
) {}