package at.atypicalbacklog.management.dto.dlc;

import java.time.LocalDate;

public record DlcResponse(
    String uid,
    String gameInstanceUid,
    String name,
    LocalDate receivedDate,
    LocalDate releaseDate,
    String playthroughUid,
    String completionStateUid
) {}
