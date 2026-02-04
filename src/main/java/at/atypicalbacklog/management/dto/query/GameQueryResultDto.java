package at.atypicalbacklog.management.dto.query;

import java.time.LocalDate;

public record GameQueryResultDto(
    String gameUid,
    String name,
    LocalDate releaseDate,
    String thumbnailImageUid
) {}
