package at.atypicalbacklog.management.dto.playthrough.query;

import java.time.Instant;

public record ActivePlaythroughOverviewRow(
    String playthroughUid,
    String gameInstanceUid,
    String displayName,
    String coverImageUid,
    String playthroughTypeUid,
    String inputUid,
    String playthroughStateUid,
    boolean isTimeTrackingActive,
    /** Start time of the current (open) time booking when isTimeTrackingActive is true; null otherwise. */
    Instant activeSessionStartedAt,
    long totalPlaytimeSeconds
) {}
