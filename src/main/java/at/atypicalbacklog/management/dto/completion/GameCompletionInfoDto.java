package at.atypicalbacklog.management.dto.completion;

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
