package at.atypicalbacklog.management.dto.statistics;

public record MonthlyPlaytimeDto(
    int year,
    int month,
    long totalSeconds
) {}