package at.atypicalbacklog.management.backlogrecommendation.dto;

import java.time.LocalDate;

public record UnifiedGameSearchCompletionDto(
    String uid,
    String gameInstanceUid,
    String dlcUid,
    String completionStateUid,
    String info,
    LocalDate finishedDate,
    LocalDate completedDate,
    String platformUid,
    String platformName,
    String coverImageUid
) {}
