package at.atypicalbacklog.management.game.dto;

import at.atypicalbacklog.management.backlog.dto.BacklogInfoDto;
import at.atypicalbacklog.management.completion.dto.GameCompletionInfoDto;

import java.time.LocalDate;
import java.util.List;

public record GameInstanceDetailResponse(
    String uid,
    String gameUid,
    String platformUid,
    String variantUid,

    String name,
    LocalDate releaseDate,
    String originalConsoleUid,
    LocalDate receivedDate,

    boolean forFree,
    boolean inPossession,

    String externalIgdbId,

    String coverImageUid,

    // optional enrichments: all backlog entries for this instance's game
    List<BacklogInfoDto> backlogEntries,
    List<GameCompletionInfoDto> completions
) {}
