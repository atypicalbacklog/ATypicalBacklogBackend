package at.atypicalbacklog.management.timetracking.dto;

public record StartSessionResponse(
    String timeBookingUid,
    java.time.Instant startedAt
) {}
