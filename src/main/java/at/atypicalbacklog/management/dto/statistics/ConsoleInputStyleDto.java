package at.atypicalbacklog.management.dto.statistics;

public record ConsoleInputStyleDto(
    int year,
    String consoleUid,
    String inputUid,
    long totalSeconds
) {}