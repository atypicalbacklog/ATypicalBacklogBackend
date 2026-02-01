package at.atypicalbacklog.management.dto.backlog;

import java.time.LocalDate;

public record RemoveGameFromBacklogRequest(
    LocalDate removedDate
) {}
