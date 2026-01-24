package at.atypicalbacklog.management.dto;

public record StopSessionResponse(
    String timeBookingUid,
    java.time.Instant startedAt,
    java.time.Instant stoppedAt,
    long durationSeconds
) {}
