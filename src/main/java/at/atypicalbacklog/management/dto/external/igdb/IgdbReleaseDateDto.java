package at.atypicalbacklog.management.dto.external.igdb;

import java.time.LocalDate;

public record IgdbReleaseDateDto(
    LocalDate date,
    Long platformId,
    String platformAbbreviation,
    Integer region
) {}
