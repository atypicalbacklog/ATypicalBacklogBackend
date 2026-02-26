package at.atypicalbacklog.management.completion.dto;

import java.time.LocalDate;

public record GameCompletionInfoDto(
    String uid,
    String gameInstanceUid,
    String dlcUid,
    String completionStateUid,
    String info,
    LocalDate finishedDate,
    LocalDate completedDate
) {}
