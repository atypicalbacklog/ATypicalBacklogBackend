package at.atypicalbacklog.management.dto.query;

import java.time.LocalDate;

public record GameOverviewRowDto(

    // Identity
    String gameUid,
    String gameInstanceUid,
    boolean hasInstance,

    // Game metadata
    String gameName,
    LocalDate gameReleaseDate,

    // Instance metadata
    String instanceName,
    LocalDate instanceReleaseDate,
    boolean inPossession,

    String platformUid,
    String platformName,
    String variantUid,
    String variantName,

    // Derived state
    boolean inBacklog,
    boolean hasActivePlaythrough,
    String completionStateUid,

    // Aggregates
    long totalPlaytimeSeconds,

    // Presentation hint
    String thumbnailImageUid

) {}
