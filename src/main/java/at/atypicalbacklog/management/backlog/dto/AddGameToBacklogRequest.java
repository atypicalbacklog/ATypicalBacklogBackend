package at.atypicalbacklog.management.backlog.dto;

import java.time.LocalDate;

public record AddGameToBacklogRequest(
    String gameUid,
    LocalDate addedDate,
    String reason
) {}
