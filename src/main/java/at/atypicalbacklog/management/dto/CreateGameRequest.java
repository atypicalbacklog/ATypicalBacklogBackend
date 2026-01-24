package at.atypicalbacklog.management.dto;

public record CreateGameRequest(
    String name,
    java.time.LocalDate firstReleaseDate
) {}
