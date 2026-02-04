package at.atypicalbacklog.management.dto.dlc;

import java.time.LocalDate;

public record DlcResponse(
    String uid,
    String gameInstanceUid,
    String name,
    LocalDate receivedDate,
    String playthroughUid
) {}
