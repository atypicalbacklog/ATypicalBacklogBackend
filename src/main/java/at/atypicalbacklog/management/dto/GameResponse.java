package at.atypicalbacklog.management.dto;

public record GameResponse(
    String uid,
    String name,
    java.time.LocalDate firstReleaseDate
) {}
