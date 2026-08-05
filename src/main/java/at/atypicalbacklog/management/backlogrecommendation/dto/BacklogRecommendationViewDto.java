package at.atypicalbacklog.management.backlogrecommendation.dto;

import at.atypicalbacklog.management.backlog.dto.BacklogInfoDto;
import at.atypicalbacklog.management.backlogrecommendation.domain.BacklogRecommendationState;
import at.atypicalbacklog.management.completion.dto.GameCompletionInfoDto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record BacklogRecommendationViewDto(
    String uid,
    Instant creationDate,
    String name,
    BacklogRecommendationState state,
    String stateReason,
    LocalDate stateDate,
    int voteCount,
    Integer voteRank,
    String originalRecommendationBy,
    /** Owner/submitter comment from create; null when sanitized for NEW. */
    String comment,
    String gameUid,
    String linkedGameImageUid,
    GameRecommendationDto gameRecommendation,
    BacklogInfoDto backlogInfo,
    List<GameCompletionInfoDto> completions,
    List<BacklogRecommendationCommentDto> comments
) {}
