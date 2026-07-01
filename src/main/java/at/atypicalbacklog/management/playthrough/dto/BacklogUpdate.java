package at.atypicalbacklog.management.playthrough.dto;

import java.time.LocalDate;

public record BacklogUpdate(
    String uid,
    LocalDate removedDate,
    String reason
) {}
