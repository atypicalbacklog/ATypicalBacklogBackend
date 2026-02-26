package at.atypicalbacklog.management.statistics.dto;

public record WeeklyPlaytimeDto(
    int year,
    int week,
    long totalSeconds
) {}