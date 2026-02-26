package at.atypicalbacklog.management.external.igdb.dto;

import java.time.LocalDate;

public record IgdbReleaseDateDto(
    LocalDate date,
    Long platformId,
    String platformAbbreviation,
    Integer region
) {}
