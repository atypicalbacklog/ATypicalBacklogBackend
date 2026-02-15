package at.atypicalbacklog.management.dto.external.igdb;

import java.time.LocalDate;
import java.util.List;

public record IgdbGameSearchResultDto(
    long externalId,
    String name,
    LocalDate releaseDate,
    String coverImageUrl,
    String category,
    List<String> releasedPlatforms,
    List<IgdbReleaseDateDto> releaseDates
) {}