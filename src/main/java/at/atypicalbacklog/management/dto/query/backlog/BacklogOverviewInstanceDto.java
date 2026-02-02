package at.atypicalbacklog.management.dto.query.backlog;

import java.time.LocalDate;

public record BacklogOverviewInstanceDto(

    String gameInstanceUid,
    String platformUid,
    String platformName,
    String originalConsoleUid,

    String variantUid,
    String instanceName,

    LocalDate releaseDate,
    LocalDate receivedDate,

    boolean inPossession,

    // Completion
    String completionStateUid,

    // Playthrough
    String activePlaythroughUid,

    // Aggregates
    long totalPlaytimeSeconds

) {}
