package at.atypicalbacklog.management.statistics.dto;

/**
 * One row from vw_game_played_per_year (per year, per game instance) plus resolved cover image UID.
 */
public record GamePlayedPerYearDto(
    int year,
    String gameInstanceUid,
    String gameName,
    int bookingCount,
    int playthroughCount,
    long totalPlaytimeSeconds,
    /** Preferred cover: instance cover if present, else game cover (same logic as elsewhere). */
    String coverImageUid
) {}
