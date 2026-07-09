package at.atypicalbacklog.management.timetracking.dto;

public record ReduceTimeBookingDurationRequest(
    String uid,
    int durationMinutes
) {}
