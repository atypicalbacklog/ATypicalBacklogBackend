package at.atypicalbacklog.management.backlog.dto;

import java.time.LocalDate;

public record RemoveGameFromBacklogRequest(
    LocalDate removedDate
) {}
