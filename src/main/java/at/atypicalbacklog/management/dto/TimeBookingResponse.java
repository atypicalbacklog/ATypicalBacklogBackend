package at.atypicalbacklog.management.dto;

import java.time.Instant;

public record TimeBookingResponse(
    String uid,
    Instant creationDate,
    String playthroughUid,
    Instant startTime,
    Instant endTime
) {}
