package at.atypicalbacklog.management.importing.dto;

import at.atypicalbacklog.management.importing.ResolutionStatus;

public record UpdateStagedGroupRequest(
    String stagedGameUid,
    String importedName,
    String inferredTypeUid,
    Integer inferredYear,
    String proposedPlaythroughStateUid,
    /** Intention: UPDATED, CURATED, or FINALIZED */
    ResolutionStatus resolutionStatus
) {}