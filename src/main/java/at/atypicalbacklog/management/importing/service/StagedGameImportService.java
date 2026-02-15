package at.atypicalbacklog.management.importing.service;

import at.atypicalbacklog.management.domain.*;
import at.atypicalbacklog.management.domain.image.ImageOwnerType;
import at.atypicalbacklog.management.importing.ResolutionStatus;
import at.atypicalbacklog.management.importing.domain.StagedGame;
import at.atypicalbacklog.management.importing.domain.StagedPlaythroughGroup;
import at.atypicalbacklog.management.importing.domain.StagedTimeBookingRaw;
import at.atypicalbacklog.management.importing.repository.StagedGameRepository;
import at.atypicalbacklog.management.importing.repository.StagedPlaythroughGroupRepository;
import at.atypicalbacklog.management.importing.repository.StagedTimeBookingRawRepository;
import at.atypicalbacklog.management.repository.*;
import at.atypicalbacklog.management.service.external.igdb.IgdbImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

/**
 * Imports a finalized StagedGame (and its linked StagedPlaythroughGroups and time bookings)
 * into production tables in one transaction: game, game_instance, game_completion, game_image,
 * playthrough, time_booking, backlog. On success, sets ResolutionStatus to IMPORTED for the
 * staged game and its playthrough groups.
 */
@Service
public class StagedGameImportService {

    private static final Logger log = LoggerFactory.getLogger(StagedGameImportService.class);

    private final StagedGameRepository stagedGameRepo;
    private final StagedPlaythroughGroupRepository stagedGroupRepo;
    private final StagedTimeBookingRawRepository stagedTimeBookingRepo;
    private final GameRepository gameRepo;
    private final GameInstanceRepository gameInstanceRepo;
    private final GameCompletionRepository gameCompletionRepo;
    private final PlaythroughRepository playthroughRepo;
    private final TimeBookingRepository timeBookingRepo;
    private final BacklogRepository backlogRepo;
    private final IgdbImportService igdbImportService;

    public StagedGameImportService(
        StagedGameRepository stagedGameRepo,
        StagedPlaythroughGroupRepository stagedGroupRepo,
        StagedTimeBookingRawRepository stagedTimeBookingRepo,
        GameRepository gameRepo,
        GameInstanceRepository gameInstanceRepo,
        GameCompletionRepository gameCompletionRepo,
        PlaythroughRepository playthroughRepo,
        TimeBookingRepository timeBookingRepo,
        BacklogRepository backlogRepo,
        IgdbImportService igdbImportService
    ) {
        this.stagedGameRepo = stagedGameRepo;
        this.stagedGroupRepo = stagedGroupRepo;
        this.stagedTimeBookingRepo = stagedTimeBookingRepo;
        this.gameRepo = gameRepo;
        this.gameInstanceRepo = gameInstanceRepo;
        this.gameCompletionRepo = gameCompletionRepo;
        this.playthroughRepo = playthroughRepo;
        this.timeBookingRepo = timeBookingRepo;
        this.backlogRepo = backlogRepo;
        this.igdbImportService = igdbImportService;
    }

    /**
     * Imports the given staged game (must be FINALIZED) into production in one transaction.
     * Inserts: game (if new), game_instance (if new), game_completion, game_image (if IGDB id present),
     * playthrough(s), time_booking(s), backlog. Then sets staged game and its groups to IMPORTED.
     */
    @Transactional
    public void importFinalizedStagedGame(StagedGame staged) {
        if (staged.getResolutionStatus() != ResolutionStatus.FINALIZED) {
            throw new IllegalArgumentException("StagedGame must be FINALIZED to import: " + staged.getUid());
        }

        if (staged.getProposedCompletionStateUid() == null || staged.getProposedCompletionStateUid().isBlank()) {
            throw new IllegalStateException(
                "Cannot import StagedGame: proposedCompletionStateUid (game completion) must be set. StagedGame uid=" + staged.getUid());
        }

        List<StagedPlaythroughGroup> groups = stagedGroupRepo.findByStagedGameUid(staged.getUid());
        for (StagedPlaythroughGroup group : groups) {
            if (group.getProposedPlaythroughStateUid() == null || group.getProposedPlaythroughStateUid().isBlank()) {
                throw new IllegalStateException(
                    "Cannot import StagedGame: proposedPlaythroughStateUid must be set for every linked playthrough group. Group uid=" + group.getUid());
            }
        }

        // 1. Game
        String gameUid = staged.getMatchedGameUid();
        if (gameUid == null || gameUid.isBlank()) {
            gameUid = UUID.randomUUID().toString();
            Game game = new Game(
                gameUid,
                staged.getProposedName() != null ? staged.getProposedName() : "Imported",
                staged.getProposedReleaseDate()
            );
            gameRepo.save(game);
            log.debug("Created game: uid={}", gameUid);
        }

        // 2. GameInstance
        String gameInstanceUid = staged.getMatchedGameInstanceUid();
        if (gameInstanceUid == null || gameInstanceUid.isBlank()) {
            String platformUid = staged.getProposedPlatformUid();
            String variantUid = staged.getProposedVariantUid();
            if (platformUid == null || platformUid.isBlank() || variantUid == null || variantUid.isBlank()) {
                throw new IllegalStateException(
                    "Cannot create new game instance: proposedPlatformUid and proposedVariantUid must be set when not matching an existing instance.");
            }
            gameInstanceUid = UUID.randomUUID().toString();
            GameInstance instance = new GameInstance(
                gameInstanceUid,
                Instant.now(),
                staged.getProposedName(),
                staged.getProposedReleaseDate(),
                gameUid,
                platformUid,
                staged.getMatchedOriginalConsoleUid(),
                variantUid,
                staged.getProposedReceivedDate(),
                false,
                true,
                staged.getMatchedExternalIgdbId()
            );
            gameInstanceRepo.save(instance);
            log.debug("Created game_instance: uid={}", gameInstanceUid);
        }

        // 3. GameCompletion
        String completionStateUid = staged.getProposedCompletionStateUid();
        if (completionStateUid != null && !completionStateUid.isBlank()) {
            String completionUid = UUID.randomUUID().toString();
            GameCompletion completion = new GameCompletion(
                completionUid,
                gameInstanceUid,
                completionStateUid,
                null,
                null
            );
            gameCompletionRepo.save(completion);
            log.debug("Created game_completion: uid={}", completionUid);
        }

        // 4. Game_image (via IGDB cover when external id present)
        if (staged.getMatchedExternalIgdbId() != null && !staged.getMatchedExternalIgdbId().isBlank()) {
            try {
                long igdbId = Long.parseLong(staged.getMatchedExternalIgdbId().trim());
                igdbImportService.importCover(
                    igdbId,
                    ImageOwnerType.GAME,
                    gameUid,
                    gameInstanceUid,
                    null
                );
                log.debug("Created game_image for game from IGDB: gameUid={}", gameUid);
            } catch (NumberFormatException e) {
                log.warn("Skipping game_image: matchedExternalIgdbId is not numeric: {}", staged.getMatchedExternalIgdbId());
            }
        }

        // 5. Playthroughs and 6. TimeBookings (per group): load staged time bookings first, then create playthrough with startedAt = date of earliest booking
        for (StagedPlaythroughGroup group : groups) {
            String playthroughStateUid = group.getProposedPlaythroughStateUid();
            List<StagedTimeBookingRaw> rawBookings = stagedTimeBookingRepo.findByStagedPlaythroughGroupUid(group.getUid());
            LocalDate startedAt = rawBookings.stream()
                .map(StagedTimeBookingRaw::getStartTime)
                .min(Instant::compareTo)
                .map(instant -> instant.atZone(ZoneOffset.UTC).toLocalDate())
                .orElse(null);
            int year = group.getInferredYear() != null ? group.getInferredYear() : (startedAt != null ? startedAt.getYear() : LocalDate.now().getYear());
            String playthroughUid = UUID.randomUUID().toString();
            Playthrough playthrough = new Playthrough(
                playthroughUid,
                gameInstanceUid,
                startedAt,
                year,
                playthroughStateUid
            );
            if (group.getInferredTypeUid() != null) {
                playthrough.updateMetadata(group.getInferredTypeUid(), null, null, playthroughStateUid);
            }
            playthroughRepo.save(playthrough);

            for (StagedTimeBookingRaw raw : rawBookings) {
                TimeBooking tb = new TimeBooking(
                    UUID.randomUUID().toString(),
                    playthroughUid,
                    raw.getStartTime()
                );
                tb.stop(raw.getEndTime());
                timeBookingRepo.save(tb);
            }
        }

        // 7. Backlog
        if (staged.getBacklogStartDate() != null) {
            String backlogUid = UUID.randomUUID().toString();
            BacklogEntry entry = new BacklogEntry(
                backlogUid,
                gameUid,
                staged.getBacklogStartDate(),
                staged.getBacklogReason()
            );
            if (staged.getBacklogEndDate() != null) {
                entry.remove(staged.getBacklogEndDate());
            }
            backlogRepo.save(entry);
            log.debug("Created backlog entry: uid={}", backlogUid);
        }

        // Mark staged game and all its groups as IMPORTED
        staged.setResolutionStatus(ResolutionStatus.IMPORTED);
        stagedGameRepo.save(staged);
        for (StagedPlaythroughGroup group : groups) {
            group.setResolutionStatus(ResolutionStatus.IMPORTED);
            stagedGroupRepo.save(group);
        }
        log.info("Imported staged game {} and {} groups; set status to IMPORTED", staged.getUid(), groups.size());
    }
}
