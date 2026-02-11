package at.atypicalbacklog.management.importing.dto;

import at.atypicalbacklog.management.importing.ResolutionStatus;

import java.time.LocalDate;
import java.util.List;

public record StagedGameDetailDto(
    String uid,
    String importBatchUid,
    String proposedName,
    LocalDate proposedReleaseDate,
    LocalDate proposedReceivedDate,
    String proposedVariantUid,
    String proposedPlatformUid,
    String matchedGameUid,
    String matchedExternalIgdbId,
    String matchedOriginalConsoleUid,
    String proposedCompletionStateUid,
    LocalDate backlogStartDate,
    LocalDate backlogEndDate,
    String backlogReason,
    String matchedGameInstanceUid,
    ResolutionStatus resolutionStatus,
    List<StagedPlaythroughGroupSummaryDto> playthroughGroups
) {}
