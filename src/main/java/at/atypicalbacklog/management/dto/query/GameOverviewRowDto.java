package at.atypicalbacklog.management.dto.query;

import java.time.LocalDate;

public record GameOverviewRowDto(

    // Row type: true = DLC row, false = instance row
    boolean isDlc,

    // Identity
    String gameUid,
    String gameInstanceUid,
    String dlcUid,           // non-null when isDlc
    boolean hasInstance,

    // Game metadata
    String gameName,
    LocalDate gameReleaseDate,

    // Instance metadata
    String instanceName,
    LocalDate instanceReleaseDate,
    LocalDate instanceReceivedDate,
    boolean inPossession,

    String platformUid,
    String platformName,
    String variantUid,
    String variantName,

    // Instance cover (game instance cover when available)
    String instanceCoverImageUid,

    // DLC-specific (null when instance row)
    String dlcName,
    LocalDate dlcReleaseDate,

    // Derived state (from instance when instance row; from DLC playthrough when DLC row; null when DLC not linked)
    boolean inBacklog,
    Boolean hasActivePlaythrough,
    String completionStateUid,

    // Aggregates (from instance or DLC playthrough; null when DLC not linked)
    Long totalPlaytimeSeconds,

    // Presentation: game/instance fallback cover
    String coverImageUid,

    // Instance has at least one DLC
    boolean hasDlc

) {}
