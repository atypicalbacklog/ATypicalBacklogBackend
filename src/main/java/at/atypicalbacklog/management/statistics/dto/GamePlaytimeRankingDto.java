package at.atypicalbacklog.management.statistics.dto;

public record GamePlaytimeRankingDto(
    String gameUid,
    long totalSeconds
) {}