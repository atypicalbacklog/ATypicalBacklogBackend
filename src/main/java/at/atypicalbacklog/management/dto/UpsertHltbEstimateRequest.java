package at.atypicalbacklog.management.dto;

/**
 * Insert or update an HLTB estimate.
 * If uid is null or no record exists with that uid, a new record is created.
 */
public record UpsertHltbEstimateRequest(
    String uid,
    String gameInstanceUid,
    Float mainHours,
    Float extraHours,
    Float completeHours,
    String externalId
) {}
