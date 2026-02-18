package at.atypicalbacklog.management.dto.statistics;

public record BookingDurationDistributionDto(
    int durationMinutes,
    long bookingCount
) {}