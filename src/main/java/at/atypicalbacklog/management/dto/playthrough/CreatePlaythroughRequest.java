package at.atypicalbacklog.management.dto.playthrough;

import java.time.LocalDate;

public record CreatePlaythroughRequest(
    String gameInstanceUid,
    LocalDate startedAt,
    String playthroughTypeUid
) {}
