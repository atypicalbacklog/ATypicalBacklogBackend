package at.atypicalbacklog.management.importing.dto;

import at.atypicalbacklog.management.importing.ResolutionStatus;

public record StagedGameSummaryDto(
    String uid,
    String proposedName,
    ResolutionStatus resolutionStatus
) {}
