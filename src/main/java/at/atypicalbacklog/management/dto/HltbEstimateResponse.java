package at.atypicalbacklog.management.dto;

import java.time.Instant;

public record HltbEstimateResponse(
    String uid,
    String gameInstanceUid,
    Float mainHours,
    Float extraHours,
    Float completeHours,
    String externalId,
    Instant lastUpdated
) {}
