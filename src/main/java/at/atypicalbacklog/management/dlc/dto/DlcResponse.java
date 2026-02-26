package at.atypicalbacklog.management.dlc.dto;

import java.time.LocalDate;

public record DlcResponse(
    String uid,
    String gameInstanceUid,
    String name,
    LocalDate receivedDate,
    LocalDate releaseDate,
    String playthroughUid,
    String completionStateUid,
    String coverImageUid,
    LocalDate finishedDate,
    LocalDate completedDate
) {}
