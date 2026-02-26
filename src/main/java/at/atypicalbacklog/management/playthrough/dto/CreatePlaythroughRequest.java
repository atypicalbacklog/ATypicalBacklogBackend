package at.atypicalbacklog.management.playthrough.dto;

import java.time.LocalDate;

public record CreatePlaythroughRequest(
    String gameInstanceUid,
    LocalDate startedAt,
    String playthroughTypeUid,  // optional
    String inputUid              // optional
) {}
