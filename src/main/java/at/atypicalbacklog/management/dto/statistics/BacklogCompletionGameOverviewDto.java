package at.atypicalbacklog.management.dto.statistics;

import java.time.LocalDate;

/**
 * One row from backlog_completion_game_overview (filtered by year) plus resolved cover image UID.
 */
public record BacklogCompletionGameOverviewDto(
    String gameInstanceUid,
    String gameName,
    String platform,
    boolean forFree,
    LocalDate receivedDate,
    LocalDate releaseDate,
    LocalDate addedToBacklog,
    LocalDate finishedDate,
    LocalDate completedDate,
    LocalDate removedFromBacklog,
    String gameUid,
    String backlogUid,
    String platformUid,
    String gameCompletionUid,
    String variantUid,
    String consoleUid,
    /** Preferred cover: instance cover if present, else game cover (same logic as elsewhere). */
    String coverImageUid
) {}
