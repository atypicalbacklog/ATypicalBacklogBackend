package at.atypicalbacklog.management.publicfeed.dto;

import at.atypicalbacklog.management.game.dto.GameOverviewRowDto;

public record PublicGameFeedRowDto(
    String gameUid,
    String gameInstanceUid,
    String imageUid,
    String name,
    GameOverviewRowDto overview
) {}
