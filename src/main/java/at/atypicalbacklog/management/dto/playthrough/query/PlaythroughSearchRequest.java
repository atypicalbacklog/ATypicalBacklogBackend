package at.atypicalbacklog.management.dto.playthrough.query;

public record PlaythroughSearchRequest(
    String name,
    String playthroughStateUid,
    String playthroughTypeUid,
    Integer year
) {}