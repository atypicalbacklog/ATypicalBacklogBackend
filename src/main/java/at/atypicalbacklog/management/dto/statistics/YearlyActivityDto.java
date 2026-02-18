package at.atypicalbacklog.management.dto.statistics;

public record YearlyActivityDto(
    int year,
    long totalSeconds,
    int activeDays,
    int bookingCount,
    int distinctGames,
    int completionCount
) {}