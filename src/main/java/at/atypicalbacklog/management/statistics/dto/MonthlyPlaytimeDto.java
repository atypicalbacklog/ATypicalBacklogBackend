package at.atypicalbacklog.management.statistics.dto;

public record MonthlyPlaytimeDto(
    int year,
    int month,
    long totalSeconds
) {}