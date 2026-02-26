package at.atypicalbacklog.management.backlog.dto;

import java.time.LocalDate;
import java.util.List;

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
    long totalPlaytimeSeconds,

    // Instance-specific cover (null if none)
    String coverImageUid,

    // All playthroughs (type, state, started_at)
    List<BacklogOverviewPlaythroughDto> playthroughs,

    // All DLCs (name)
    List<String> dlcNames

) {}
