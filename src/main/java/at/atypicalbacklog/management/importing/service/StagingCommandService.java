package at.atypicalbacklog.management.importing.service;

import at.atypicalbacklog.management.importing.ResolutionStatus;
import at.atypicalbacklog.management.importing.domain.StagedGame;
import at.atypicalbacklog.management.importing.domain.StagedPlaythroughGroup;
import at.atypicalbacklog.management.importing.dto.UpdateStagedGameRequest;
import at.atypicalbacklog.management.importing.dto.UpdateStagedGroupRequest;
import at.atypicalbacklog.management.importing.repository.StagedGameRepository;
import at.atypicalbacklog.management.importing.repository.StagedPlaythroughGroupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StagingCommandService {

    private final StagedGameRepository gameRepo;
    private final StagedPlaythroughGroupRepository groupRepo;
    private final StagedGameImportService importService;

    public StagingCommandService(
        StagedGameRepository gameRepo,
        StagedPlaythroughGroupRepository groupRepo,
        StagedGameImportService importService
    ) {
        this.gameRepo = gameRepo;
        this.groupRepo = groupRepo;
        this.importService = importService;
    }

    @Transactional
    public StagedGame updateStagedGame(String uid, UpdateStagedGameRequest req) {
        StagedGame game = gameRepo.findById(uid)
            .orElseThrow(() -> new IllegalArgumentException("StagedGame not found: " + uid));
        if (req.proposedName() != null) game.setProposedName(req.proposedName());
        if (req.proposedReleaseDate() != null) game.setProposedReleaseDate(req.proposedReleaseDate());
        if (req.proposedReceivedDate() != null) game.setProposedReceivedDate(req.proposedReceivedDate());
        if (req.proposedVariantUid() != null) game.setProposedVariantUid(req.proposedVariantUid());
        if (req.proposedPlatformUid() != null) game.setProposedPlatformUid(req.proposedPlatformUid());
        if (req.matchedGameUid() != null) game.setMatchedGameUid(req.matchedGameUid());
        if (req.matchedGameInstanceUid() != null) game.setMatchedGameInstanceUid(req.matchedGameInstanceUid());
        if (req.matchedExternalIgdbId() != null) game.setMatchedExternalIgdbId(req.matchedExternalIgdbId());
        if (req.matchedOriginalConsoleUid() != null) game.setMatchedOriginalConsoleUid(req.matchedOriginalConsoleUid());
        if (req.proposedCompletionStateUid() != null) game.setProposedCompletionStateUid(req.proposedCompletionStateUid());
        if (req.backlogStartDate() != null) game.setBacklogStartDate(req.backlogStartDate());
        if (req.backlogEndDate() != null) game.setBacklogEndDate(req.backlogEndDate());
        if (req.backlogReason() != null) game.setBacklogReason(req.backlogReason());
        if (req.proposedFinishedDate() != null) game.setProposedFinishedDate(req.proposedFinishedDate());
        if (req.resolutionStatus() != null) game.setResolutionStatus(req.resolutionStatus());
        StagedGame saved = gameRepo.save(game);
        if (saved.getResolutionStatus() == ResolutionStatus.FINALIZED) {
            importService.importFinalizedStagedGame(saved);
        }
        return saved;
    }

    @Transactional
    public StagedPlaythroughGroup updateStagedPlaythroughGroup(String uid, UpdateStagedGroupRequest req) {
        StagedPlaythroughGroup group = groupRepo.findById(uid)
            .orElseThrow(() -> new IllegalArgumentException("StagedPlaythroughGroup not found: " + uid));
        if (req.stagedGameUid() != null) group.setStagedGameUid(req.stagedGameUid());
        if (req.importedName() != null) group.setImportedPlaythroughName(req.importedName());
        if (req.inferredTypeUid() != null) group.setInferredTypeUid(req.inferredTypeUid());
        if (req.inferredYear() != null) group.setInferredYear(req.inferredYear());
        if (req.proposedPlaythroughStateUid() != null) group.setProposedPlaythroughStateUid(req.proposedPlaythroughStateUid());
        if (req.resolutionStatus() != null) group.setResolutionStatus(req.resolutionStatus());
        return groupRepo.save(group);
    }
}
