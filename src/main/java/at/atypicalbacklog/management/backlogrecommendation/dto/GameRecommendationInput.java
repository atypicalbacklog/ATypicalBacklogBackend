package at.atypicalbacklog.management.backlogrecommendation.dto;

import java.time.LocalDate;

public record GameRecommendationInput(
    String uid,
    String nameBase,
    LocalDate releaseDate,
    String externalIgdbId
) {}
