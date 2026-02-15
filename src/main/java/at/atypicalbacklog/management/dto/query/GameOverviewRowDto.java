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

    // Covers (each on its own; client may choose which to display)
    String gameCoverImageUid,       // game-level cover (owner_type GAME)
    String instanceCoverImageUid,   // game instance cover (owner_type GAME_INSTANCE)
    String dlcCoverImageUid,        // DLC cover (owner_type DLC; non-null only when isDlc and DLC has cover)

    // DLC-specific (null when instance row)
    String dlcName,
    LocalDate dlcReleaseDate,

    // Derived state (from instance when instance row; from DLC playthrough when DLC row; null when DLC not linked)
    boolean inBacklog,
    Boolean hasActivePlaythrough,
    String completionStateUid,

    // Aggregates (from instance or DLC playthrough; null when DLC not linked)
    Long totalPlaytimeSeconds,

    // Instance has at least one DLC
    boolean hasDlc

) {}
