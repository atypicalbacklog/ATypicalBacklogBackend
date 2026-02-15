package at.atypicalbacklog.management.importing.dto;

import at.atypicalbacklog.management.importing.ResolutionStatus;

import java.time.LocalDate;

public record UpdateStagedGameRequest(
    String proposedName,
    LocalDate proposedReleaseDate,
    LocalDate proposedReceivedDate,
    String proposedVariantUid,
    String proposedPlatformUid,
    String matchedGameUid,
    String matchedGameInstanceUid,
    String matchedExternalIgdbId,
    String matchedOriginalConsoleUid,
    String proposedCompletionStateUid,
    LocalDate backlogStartDate,
    LocalDate backlogEndDate,
    String backlogReason,
    /** Intention: UPDATED, CURATED, or FINALIZED */
    ResolutionStatus resolutionStatus
) {}