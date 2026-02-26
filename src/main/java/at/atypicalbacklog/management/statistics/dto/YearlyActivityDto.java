package at.atypicalbacklog.management.statistics.dto;

public record YearlyActivityDto(
    int year,
    long totalSeconds,
    int activeDays,
    int bookingCount,
    int distinctGames,
    int completionCount
) {}