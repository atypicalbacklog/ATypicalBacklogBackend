package at.atypicalbacklog.management.playthrough.dto;

import java.time.LocalDate;

public record PlaythroughQueryResultDto(
    String playthroughUid,
    String playthroughStateUid,
    String playthroughTypeUid,
    String inputUid,
    LocalDate startedAt
) {}
