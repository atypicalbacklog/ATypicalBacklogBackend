package at.atypicalbacklog.management.dto.statistics;

public record GamePlaytimeRankingDto(
    String gameUid,
    long totalSeconds
) {}