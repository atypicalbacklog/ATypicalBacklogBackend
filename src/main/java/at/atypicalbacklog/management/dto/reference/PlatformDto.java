package at.atypicalbacklog.management.dto.reference;

public record PlatformDto(
    String uid,
    String name,
    String consoleUid,
    boolean hideInChoices
) {}
