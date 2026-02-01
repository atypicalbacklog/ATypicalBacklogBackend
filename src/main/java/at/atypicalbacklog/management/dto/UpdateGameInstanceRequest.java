package at.atypicalbacklog.management.dto;

import java.time.LocalDate;

public record UpdateGameInstanceRequest(
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
