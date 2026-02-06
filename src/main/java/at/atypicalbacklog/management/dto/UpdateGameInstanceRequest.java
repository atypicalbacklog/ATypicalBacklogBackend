package at.atypicalbacklog.management.dto;

import java.time.LocalDate;

public record UpdateGameInstanceRequest(
    /** If set, instance is relinked to this game; if null, current game remains. */
    String gameUid,

    String platformUid,
    String variantUid,

    // optional
    String name,
    LocalDate releaseDate,
    String originalConsoleUid,
    LocalDate receivedDate,

    Boolean forFree,
    Boolean inPossession
) {}
