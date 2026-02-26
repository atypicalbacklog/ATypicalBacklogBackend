package at.atypicalbacklog.management.backlog.dto;

import java.time.LocalDate;
import java.util.List;

public record BacklogOverviewGameDto(

    // Game identity
    String gameUid,
    String gameName,
    LocalDate gameReleaseDate,

    // Backlog info
    String backlogUid,
    LocalDate backlogAddedDate,
    String backlogReason,

    // Derived ordering
    int backlogScore,

    // Aggregated flags
    boolean hasAnyPlaythrough,
    boolean hasActivePlaythrough,

    // Children
    List<BacklogOverviewInstanceDto> instances,

    // Presentation hint
    String coverImageUid

) {}
