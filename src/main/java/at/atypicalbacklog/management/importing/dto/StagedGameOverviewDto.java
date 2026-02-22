package at.atypicalbacklog.management.importing.dto;

import java.time.LocalDate;

public record StagedGameOverviewDto(
    String stagedGameUid,
    String proposedName,
    String proposedPlatformUid,
    String proposedCompletionStateUid,
    LocalDate proposedFinishedDate,
    String resolutionStatus,
    String curationStatus
) {}