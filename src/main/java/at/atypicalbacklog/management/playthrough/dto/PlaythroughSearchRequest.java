package at.atypicalbacklog.management.playthrough.dto;

public record PlaythroughSearchRequest(
    String name,
    String playthroughStateUid,
    String playthroughTypeUid,
    Integer year
) {}