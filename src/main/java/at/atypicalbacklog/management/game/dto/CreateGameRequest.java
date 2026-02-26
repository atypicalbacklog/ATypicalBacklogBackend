package at.atypicalbacklog.management.game.dto;

public record CreateGameRequest(
    String name,
    java.time.LocalDate firstReleaseDate
) {}
