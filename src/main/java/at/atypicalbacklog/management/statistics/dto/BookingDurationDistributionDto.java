package at.atypicalbacklog.management.statistics.dto;

public record BookingDurationDistributionDto(
    int durationMinutes,
    long bookingCount
) {}