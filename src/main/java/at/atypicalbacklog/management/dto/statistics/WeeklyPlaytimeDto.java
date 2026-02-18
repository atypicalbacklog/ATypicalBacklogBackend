package at.atypicalbacklog.management.dto.statistics;

public record WeeklyPlaytimeDto(
    int year,
    int week,
    long totalSeconds
) {}