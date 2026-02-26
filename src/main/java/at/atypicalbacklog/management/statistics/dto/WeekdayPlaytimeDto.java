package at.atypicalbacklog.management.statistics.dto;

public record WeekdayPlaytimeDto(
    int year,
    int weekday,
    long totalSeconds
) {}