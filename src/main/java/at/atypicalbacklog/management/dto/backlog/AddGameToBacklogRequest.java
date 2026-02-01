package at.atypicalbacklog.management.dto.backlog;

import java.time.LocalDate;

public record AddGameToBacklogRequest(
    String gameUid,
    LocalDate addedDate,
    String reason
) {}
