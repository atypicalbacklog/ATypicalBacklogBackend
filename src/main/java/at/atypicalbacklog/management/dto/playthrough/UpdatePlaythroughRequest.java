package at.atypicalbacklog.management.dto.playthrough;

import java.time.LocalDate;

public record UpdatePlaythroughRequest(
    String playthroughTypeUid,
    String inputUid,
    LocalDate startedAt,
    String playthroughStateUid
) {}
