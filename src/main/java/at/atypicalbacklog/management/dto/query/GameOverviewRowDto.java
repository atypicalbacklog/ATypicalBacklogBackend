package at.atypicalbacklog.management.dto.query;

import java.time.Duration;

public record GameOverviewRowDto(

    // Identity
    String gameUid,
    String gameInstanceUid,     // null if no instance exists
    boolean hasInstance,

    // Game metadata
    String gameName,
    Integer releaseYear,

    // Instance metadata
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
