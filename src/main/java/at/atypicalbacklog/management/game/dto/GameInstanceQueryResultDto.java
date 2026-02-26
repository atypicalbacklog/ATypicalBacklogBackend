package at.atypicalbacklog.management.game.dto;

public record GameInstanceQueryResultDto(
    String gameInstanceUid,
    String name,
    String platformUid,
    String coverImageUid
) {}
