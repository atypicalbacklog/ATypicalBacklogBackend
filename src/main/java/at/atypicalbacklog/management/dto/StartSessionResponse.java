package at.atypicalbacklog.management.dto;

public record StartSessionResponse(
    String timeBookingUid,
    java.time.Instant startedAt
) {}
