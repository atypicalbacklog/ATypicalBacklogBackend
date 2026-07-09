package at.atypicalbacklog.management.backlogrecommendation.dto;

import java.time.Instant;
import java.time.LocalDate;

public record GameRecommendationDto(
    String uid,
    Instant creationDate,
    String nameBase,
    LocalDate releaseDate,
    String externalIgdbId
) {}
