package at.atypicalbacklog.management.service;

import at.atypicalbacklog.management.domain.*;
import at.atypicalbacklog.management.dto.*;
import at.atypicalbacklog.management.dto.backlog.BacklogInfoDto;
import at.atypicalbacklog.management.dto.completion.GameCompletionInfoDto;
import at.atypicalbacklog.management.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameInstanceDetailQueryService {

    private final GameInstanceRepository gameInstanceRepo;
    private final BacklogRepository backlogRepo;
    private final GameCompletionRepository completionRepo;

    public GameInstanceDetailQueryService(
        GameInstanceRepository gameInstanceRepo,
        BacklogRepository backlogRepo,
        GameCompletionRepository completionRepo
    ) {
        this.gameInstanceRepo = gameInstanceRepo;
        this.backlogRepo = backlogRepo;
        this.completionRepo = completionRepo;
    }

    public GameInstanceDetailResponse getByUid(String uid) {

        GameInstance gi = gameInstanceRepo.findById(uid)
            .orElseThrow(() -> new IllegalArgumentException("GameInstance not found"));

        BacklogInfoDto backlog = resolveBacklog(gi.getGameUid());

        List<GameCompletionInfoDto> completions =
            completionRepo.findByGameInstanceUidOrderByCreationDateAsc(uid)
                .stream()
                .map(gc -> new GameCompletionInfoDto(
                    gc.getUid(),
                    gc.getGameInstanceUid(),
                    gc.getDlcUid(),
                    gc.getCompletionStateUid(),
                    gc.getInfo()
                ))
                .toList();

        return new GameInstanceDetailResponse(
            gi.getUid(),
            gi.getGameUid(),
            gi.getPlatformUid(),
            gi.getVariantUid(),
            gi.getName(),
            gi.getReleaseDate(),
            gi.getOriginalConsoleUid(),
            gi.getReceivedDate(),
            gi.isForFree(),
            gi.isInPossession(),
            backlog,
            completions
        );
    }

    private BacklogInfoDto resolveBacklog(String gameUid) {

        return backlogRepo.findFirstByGameUidAndRemovedDateIsNull(gameUid)
            .or(() ->
                backlogRepo.findFirstByGameUidAndRemovedDateIsNotNullOrderByRemovedDateDesc(gameUid)
            )
            .map(b -> new BacklogInfoDto(
                b.getUid(),
                b.getAddedDate(),
                b.getRemovedDate(),
                b.getReason()
            ))
            .orElse(null);
    }
}
