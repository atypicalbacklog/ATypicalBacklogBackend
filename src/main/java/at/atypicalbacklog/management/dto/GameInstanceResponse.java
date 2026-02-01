package at.atypicalbacklog.management.dto;

import java.time.LocalDate;

public record GameInstanceResponse(
    String uid,
    String gameUid,
    String platformUid,
    String variantUid,

    String name,
    LocalDate releaseDate,
    String originalConsoleUid,
    LocalDate receivedDate,

    boolean forFree,
    boolean inPossession
) {}
