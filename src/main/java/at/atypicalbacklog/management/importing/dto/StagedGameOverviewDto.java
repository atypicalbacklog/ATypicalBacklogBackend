package at.atypicalbacklog.management.importing.dto;

public record StagedGameOverviewDto(
    String stagedGameUid,
    String proposedName,
    String proposedPlatformUid,
    String proposedCompletionStateUid,
    String resolutionStatus,
    String curationStatus
) {}