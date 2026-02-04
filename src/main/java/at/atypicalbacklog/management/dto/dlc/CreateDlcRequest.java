package at.atypicalbacklog.management.dto.dlc;

import java.time.LocalDate;

public record CreateDlcRequest(
    String gameInstanceUid,
    String name,
    LocalDate receivedDate,
    String playthroughUid
) {}
