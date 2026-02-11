package at.atypicalbacklog.management.importing.dto;

public record StagedPlaythroughGroupDto(
    String groupUid,
    String stagedGameUid,
    String inferredTypeUid,
    Integer inferredYear,
    String proposedPlaythroughStateUid,
    long totalSeconds,
    int bookingCount
) {}