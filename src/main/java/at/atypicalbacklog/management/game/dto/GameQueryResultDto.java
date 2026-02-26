package at.atypicalbacklog.management.game.dto;

import java.time.LocalDate;

public record GameQueryResultDto(
    String gameUid,
    String name,
    LocalDate releaseDate,
    String coverImageUid
) {}
