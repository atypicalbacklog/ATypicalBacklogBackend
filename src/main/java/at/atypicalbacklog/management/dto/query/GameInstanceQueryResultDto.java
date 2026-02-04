package at.atypicalbacklog.management.dto.query;

public record GameInstanceQueryResultDto(
    String gameInstanceUid,
    String name,
    String platformUid,
    String thumbnailImageUid
) {}
