package at.atypicalbacklog.management.backlog.dto;

import java.time.LocalDate;

public record BacklogInfoDto(
    String uid,
    LocalDate addedDate,
    LocalDate removedDate,
    String reason
) {}
