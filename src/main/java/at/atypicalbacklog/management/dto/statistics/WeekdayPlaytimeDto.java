package at.atypicalbacklog.management.dto.statistics;

public record WeekdayPlaytimeDto(
    int year,
    int weekday,
    long totalSeconds
) {}