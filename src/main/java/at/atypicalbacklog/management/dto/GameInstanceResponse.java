package at.atypicalbacklog.management.dto;

public record GameInstanceResponse(
    String uid,
    String gameUid,
    String platformUid,
    String variantUid
) {}