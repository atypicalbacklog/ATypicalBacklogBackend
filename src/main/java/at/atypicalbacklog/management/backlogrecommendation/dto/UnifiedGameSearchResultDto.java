package at.atypicalbacklog.management.backlogrecommendation.dto;

import at.atypicalbacklog.management.backlog.dto.BacklogInfoDto;
import at.atypicalbacklog.management.backlogrecommendation.domain.BacklogRecommendationState;

import java.time.LocalDate;
import java.util.List;

public record UnifiedGameSearchResultDto(
    String resultType,
    String name,
    LocalDate releaseDate,
    String coverImageUid,
    String gameUid,
    BacklogInfoDto backlogInfo,
    List<UnifiedGameSearchCompletionDto> completions,
    String backlogRecommendationUid,
    int voteCount,
    BacklogRecommendationState state,
    String stateReason,
    LocalDate stateDate,
    String originalRecommendationBy,
    GameRecommendationDto gameRecommendation
) {}
