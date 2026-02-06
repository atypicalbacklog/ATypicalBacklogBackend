package at.atypicalbacklog.management.dto.query.backlog;

import java.time.LocalDate;

public record BacklogOverviewPlaythroughDto(
    String playthroughTypeUid,
    String playthroughStateUid,
    LocalDate startedAt
) {}
