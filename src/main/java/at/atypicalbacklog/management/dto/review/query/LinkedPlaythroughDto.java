package at.atypicalbacklog.management.dto.review.query;

public record LinkedPlaythroughDto(
    String uid,
    String playthroughTypeUid,
    String playthroughStateUid
) {}
