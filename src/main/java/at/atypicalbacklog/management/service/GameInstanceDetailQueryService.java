package at.atypicalbacklog.management.service;

import at.atypicalbacklog.management.domain.*;
import at.atypicalbacklog.management.domain.image.GameImage;
import at.atypicalbacklog.management.domain.image.ImageOwnerType;
import at.atypicalbacklog.management.domain.image.ImageType;
import at.atypicalbacklog.management.dto.*;
import at.atypicalbacklog.management.dto.backlog.BacklogInfoDto;
import at.atypicalbacklog.management.dto.completion.GameCompletionInfoDto;
import at.atypicalbacklog.management.repository.*;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class GameInstanceDetailQueryService {

    private final GameInstanceRepository gameInstanceRepo;
    private final BacklogRepository backlogRepo;
    private final GameCompletionRepository completionRepo;
    private final GameImageRepository imageRepo;

    public GameInstanceDetailQueryService(
        GameInstanceRepository gameInstanceRepo,
        BacklogRepository backlogRepo,
        GameCompletionRepository completionRepo,
        GameImageRepository imageRepo
    ) {
        this.gameInstanceRepo = gameInstanceRepo;
        this.backlogRepo = backlogRepo;
        this.completionRepo = completionRepo;
        this.imageRepo = imageRepo;
    }

    public GameInstanceDetailResponse getByUid(String uid) {
        Objects.requireNonNull(uid, "uid");
        GameInstance gi = gameInstanceRepo.findById(uid)
            .orElseThrow(() -> new IllegalArgumentException("GameInstance not found"));

        String coverImageUid = resolveInstanceCoverImageUid(uid);
        List<BacklogInfoDto> backlogEntries = resolveAllBacklogEntriesForGame(gi.getGameUid());

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
            coverImageUid,
            backlogEntries,
            completions
        );
    }

    private String resolveInstanceCoverImageUid(String gameInstanceUid) {
        return imageRepo
            .findByOwnerTypeAndTypeAndGameInstanceUid(ImageOwnerType.GAME_INSTANCE, ImageType.COVER, gameInstanceUid)
            .stream()
            .sorted(
                Comparator.comparing(GameImage::isPrimaryImage).reversed()
                    .thenComparing(GameImage::getCreationDate).reversed()
            )
            .findFirst()
            .map(img -> img.getUid())
            .orElse(null);
    }

    private List<BacklogInfoDto> resolveAllBacklogEntriesForGame(String gameUid) {
        return backlogRepo.findByGameUidOrderByAddedDateDesc(gameUid)
            .stream()
            .map(b -> new BacklogInfoDto(
                b.getUid(),
                b.getAddedDate(),
                b.getRemovedDate(),
                b.getReason()
            ))
            .collect(Collectors.toList());
    }
}
