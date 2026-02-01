package at.atypicalbacklog.management.dto.completion;

public record GameCompletionInfoDto(
    String uid,
    String gameInstanceUid,
    String dlcUid,
    String completionStateUid,
    String info
) {}
