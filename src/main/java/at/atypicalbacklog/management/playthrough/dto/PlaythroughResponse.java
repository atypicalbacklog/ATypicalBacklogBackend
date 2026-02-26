package at.atypicalbacklog.management.playthrough.dto;

import java.time.LocalDate;

public record PlaythroughResponse(
    String uid,
    String gameInstanceUid,
    String playthroughTypeUid,
    String inputUid,
    LocalDate startedAt,
    String playthroughStateUid
) {}
