package at.atypicalbacklog.management.dto;

public record CreateGameInstanceRequest(
    String gameUid,
    String platformUid,
    String variantUid,
    java.time.LocalDate receivedDate
) {}
