package at.atypicalbacklog.management.publicfeed.dto;

import at.atypicalbacklog.management.backlogrecommendation.domain.BacklogRecommendationState;
import at.atypicalbacklog.management.backlogrecommendation.dto.GameRecommendationDto;
import at.atypicalbacklog.management.game.dto.GameOverviewRowDto;

import java.time.LocalDate;

public record PublicRandomBacklogRecommendationFeedItemDto(
    String backlogRecommendationUid,
    String name,
    BacklogRecommendationState state,
    String stateReason,
    LocalDate stateDate,
    int voteCount,
    int voteRank,
    String gameUid,
    String gameInstanceUid,
    String imageUid,
    GameOverviewRowDto overview,
    GameRecommendationDto gameRecommendation
) {}
