package at.atypicalbacklog.management.dto.query;

import java.time.LocalDate;

public record PlaythroughQueryResultDto(
    String playthroughUid,
    String playthroughStateUid,
    String playthroughTypeUid,
    String inputUid,
    LocalDate startedAt
) {}
