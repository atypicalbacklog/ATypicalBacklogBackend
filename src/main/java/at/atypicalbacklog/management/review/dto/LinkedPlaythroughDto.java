package at.atypicalbacklog.management.review.dto;

public record LinkedPlaythroughDto(
    String uid,
    String playthroughTypeUid,
    String playthroughStateUid
) {}
