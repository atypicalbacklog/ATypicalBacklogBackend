package at.atypicalbacklog.management.importing.dto;

import at.atypicalbacklog.management.importing.ResolutionStatus;

public record StagedPlaythroughGroupSummaryDto(
    String uid,
    String importBatchUid,
    String stagedGameUid,
    String importedName,
    String inferredTypeUid,
    Integer inferredYear,
    String proposedPlaythroughStateUid,
    ResolutionStatus resolutionStatus
) {}
