package at.atypicalbacklog.management.dto.completion;

public record UpdateGameCompletionRequest(
    String gameInstanceUid,
    String completionStateUid,
    String dlcUid,
    String info
) {}
