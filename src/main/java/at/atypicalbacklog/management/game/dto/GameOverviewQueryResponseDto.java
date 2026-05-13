package at.atypicalbacklog.management.game.dto;

import java.util.List;

public record GameOverviewQueryResponseDto(
    List<GameOverviewRowDto> rows,
    long gameCount,
    long gameInstanceCount,
    long dlcCount,
    long returnedGameCount,
    long returnedGameInstanceCount,
    long returnedDlcCount
) {}
