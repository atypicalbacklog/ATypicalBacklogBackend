package at.atypicalbacklog.management.backlog.dto;

import java.time.LocalDate;

public record BacklogOverviewPlaythroughDto(
    String playthroughTypeUid,
    String playthroughStateUid,
    LocalDate startedAt
) {}
