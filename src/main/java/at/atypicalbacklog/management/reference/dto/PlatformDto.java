package at.atypicalbacklog.management.reference.dto;

public record PlatformDto(
    String uid,
    String name,
    String consoleUid,
    boolean hideInChoices
) {}
