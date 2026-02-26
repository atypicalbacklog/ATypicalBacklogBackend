package at.atypicalbacklog.management.statistics.dto;

public record ConsoleInputStyleDto(
    int year,
    String consoleUid,
    String inputUid,
    long totalSeconds
) {}