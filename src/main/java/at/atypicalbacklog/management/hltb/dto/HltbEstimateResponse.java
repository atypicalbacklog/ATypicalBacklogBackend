package at.atypicalbacklog.management.hltb.dto;

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
