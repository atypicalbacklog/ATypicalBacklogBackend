package at.atypicalbacklog.management.dto.backlog;

import java.time.LocalDate;

public record BacklogInfoDto(
    String uid,
    LocalDate addedDate,
    LocalDate removedDate,
    String reason
) {}
