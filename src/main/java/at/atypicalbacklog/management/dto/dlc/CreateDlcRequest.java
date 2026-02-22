package at.atypicalbacklog.management.dto.dlc;

import java.time.LocalDate;

public record CreateDlcRequest(
    String gameInstanceUid,
    String name,
    LocalDate receivedDate,
    LocalDate releaseDate,  // required on creation
    String playthroughUid,

    /** Optional: completion state UID for the DLC's game_completion. */
    String completionStateUid,

    /** Optional: info text for the DLC's game_completion. */
    String info,

    /** Optional: finished date on the DLC's game_completion. */
    LocalDate finishedDate,

    /** Optional: completed date on the DLC's game_completion. */
    LocalDate completedDate
) {}
