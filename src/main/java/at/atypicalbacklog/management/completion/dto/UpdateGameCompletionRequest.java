package at.atypicalbacklog.management.completion.dto;

import java.time.LocalDate;

public record UpdateGameCompletionRequest(
    String gameInstanceUid,
    String completionStateUid,
    String dlcUid,
    String info,
    LocalDate finishedDate,
    LocalDate completedDate
) {}
