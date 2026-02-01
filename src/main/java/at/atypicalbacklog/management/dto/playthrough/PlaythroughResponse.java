package at.atypicalbacklog.management.dto.playthrough;

import java.time.LocalDate;

public record PlaythroughResponse(
    String uid,
    String gameInstanceUid,
    String playthroughTypeUid,
    String inputUid,
    LocalDate startedAt,
    String playthroughStateUid
) {}
