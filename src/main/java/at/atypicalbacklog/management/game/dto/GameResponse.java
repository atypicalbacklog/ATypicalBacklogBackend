package at.atypicalbacklog.management.game.dto;

public record GameResponse(
    String uid,
    String name,
    java.time.LocalDate firstReleaseDate,
    String coverImageUid
) {}
