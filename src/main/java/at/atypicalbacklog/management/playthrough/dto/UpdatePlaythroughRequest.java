package at.atypicalbacklog.management.playthrough.dto;

import java.time.LocalDate;

public record UpdatePlaythroughRequest(
    String playthroughTypeUid,
    String inputUid,
    LocalDate startedAt,
    String playthroughStateUid
) {}
