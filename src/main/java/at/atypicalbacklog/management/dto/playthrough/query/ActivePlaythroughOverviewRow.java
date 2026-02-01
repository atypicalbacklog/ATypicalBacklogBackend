package at.atypicalbacklog.management.dto.playthrough.query;

public record ActivePlaythroughOverviewRow(
    String playthroughUid,
    String gameInstanceUid,
    String displayName,
    String thumbnailImageUid,
    String playthroughTypeUid,
    String inputUid,
    String playthroughStateUid,
    boolean isTimeTrackingActive,
    long totalPlaytimeSeconds
) {}
