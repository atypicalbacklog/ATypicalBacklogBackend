package at.atypicalbacklog.management.game.service;

import at.atypicalbacklog.management.backlog.dto.BacklogInfoDto;
import at.atypicalbacklog.management.backlog.repository.BacklogRepository;
import at.atypicalbacklog.management.completion.dto.GameCompletionInfoDto;
import at.atypicalbacklog.management.completion.repository.GameCompletionRepository;
import at.atypicalbacklog.management.game.domain.GameInstance;
import at.atypicalbacklog.management.game.dto.GameInstanceDetailResponse;
import at.atypicalbacklog.management.game.repository.GameInstanceRepository;
import at.atypicalbacklog.management.image.domain.GameImage;
import at.atypicalbacklog.management.image.domain.ImageOwnerType;
import at.atypicalbacklog.management.image.domain.ImageType;
import at.atypicalbacklog.management.image.repository.GameImageRepository;
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
                    gc.getInfo(),
                    gc.getFinishedDate(),
                    gc.getCompletedDate()
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
            gi.getExternalIgdbId(),
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
