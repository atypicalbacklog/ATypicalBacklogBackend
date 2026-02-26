package at.atypicalbacklog.management.dlc.dto;

import java.time.LocalDate;

public record UpdateDlcRequest(
    String name,
    LocalDate receivedDate,
    LocalDate releaseDate,

    /** Optional: completion state UID for the DLC's game_completion. */
    String completionStateUid,

    /** Optional: playthrough UID to link the DLC to. */
    String playthroughUid,

    /** Optional, default false: if true and no playthroughUid given, clears playthrough link. */
    Boolean removePreviousPlaythrough,

    /** Optional: finished date on the DLC's game_completion. */
    LocalDate finishedDate,

    /** Optional: completed date on the DLC's game_completion. */
    LocalDate completedDate
) {}
