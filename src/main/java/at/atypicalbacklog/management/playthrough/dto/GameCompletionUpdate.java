package at.atypicalbacklog.management.playthrough.dto;

import java.time.LocalDate;

public record GameCompletionUpdate(
    String uid,
    String dlcUid,
    String completionStateUid,
    String info,
    LocalDate finishedDate,
    LocalDate completedDate
) {}
