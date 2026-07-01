package at.atypicalbacklog.management.playthrough.service;

import at.atypicalbacklog.management.audit.AuditRecorder;
import at.atypicalbacklog.management.backlog.domain.BacklogEntry;
import at.atypicalbacklog.management.backlog.repository.BacklogRepository;
import at.atypicalbacklog.management.completion.domain.GameCompletion;
import at.atypicalbacklog.management.completion.repository.GameCompletionRepository;
import at.atypicalbacklog.management.dlc.domain.Dlc;
import at.atypicalbacklog.management.dlc.repository.DlcRepository;
import at.atypicalbacklog.management.playthrough.domain.Playthrough;
import at.atypicalbacklog.management.playthrough.dto.BacklogUpdate;
import at.atypicalbacklog.management.playthrough.dto.CreatePlaythroughRequest;
import at.atypicalbacklog.management.playthrough.dto.GameCompletionUpdate;
import at.atypicalbacklog.management.playthrough.dto.PlaythroughCompletionRequest;
import at.atypicalbacklog.management.playthrough.dto.PlaythroughResponse;
import at.atypicalbacklog.management.playthrough.dto.UpdatePlaythroughRequest;
import at.atypicalbacklog.management.playthrough.repository.PlaythroughRepository;
import at.atypicalbacklog.management.reference.repository.PlaythroughStateRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.Map;
import java.util.UUID;

@Service
public class PlaythroughCommandService {

    private final PlaythroughRepository repository;
    private final PlaythroughStateRepository stateRepository;
    private final GameCompletionRepository gameCompletionRepository;
    private final DlcRepository dlcRepository;
    private final BacklogRepository backlogRepository;
    private final AuditRecorder audit;

    public PlaythroughCommandService(
        PlaythroughRepository repository,
        PlaythroughStateRepository stateRepository,
        GameCompletionRepository gameCompletionRepository,
        DlcRepository dlcRepository,
        BacklogRepository backlogRepository,
        AuditRecorder audit
    ) {
        this.repository = repository;
        this.stateRepository = stateRepository;
        this.gameCompletionRepository = gameCompletionRepository;
        this.dlcRepository = dlcRepository;
        this.backlogRepository = backlogRepository;
        this.audit = audit;
    }

    /** 1. Create Playthrough */
    public PlaythroughResponse create(CreatePlaythroughRequest req) {


        String activeStateUid = stateRepository
        .findByName("Active")
        .orElseThrow(() -> new IllegalStateException(
            "PlaythroughState 'Active' not found"
        ))
        .getUid();
        Playthrough pt = new Playthrough(
            UUID.randomUUID().toString(),
            req.gameInstanceUid(),
            req.startedAt(),
            Year.now().getValue(),
            activeStateUid
        );
        pt.updateMetadata(req.playthroughTypeUid(), req.inputUid(), null, null);

        repository.save(pt);

        audit.record(
            "Start New Playthrough",
            "Playthrough",
            "CREATE",
            Map.of("playthroughUid", pt.getUid()),
            null,
            pt,
            null
        );

        return toResponse(pt);
    }

    /** 2. Update Playthrough Metadata */
    public PlaythroughResponse update(String uid, UpdatePlaythroughRequest req) {

        Playthrough existing = repository.findById(uid)
            .orElseThrow(() -> new IllegalArgumentException("Playthrough not found"));

        Playthrough before = existing;

        existing.updateMetadata(
            req.playthroughTypeUid(),
            req.inputUid(),
            req.startedAt(),
            req.playthroughStateUid()
        );

        repository.save(existing);

        audit.record(
            "Update Playthrough Metadata",
            "Playthrough",
            "UPDATE",
            Map.of("playthroughUid", uid),
            before,
            existing,
            req
        );

        return toResponse(existing);
    }

    /**
     * 3. Complete Playthrough in a single transaction:
     * updates playthrough metadata, the supplied game completions, links the given
     * DLCs to the playthrough and updates the backlog entry. Only non-null values
     * from the request are applied.
     */
    @Transactional
    public PlaythroughResponse updateCompletion(String playthroughUid, PlaythroughCompletionRequest req) {

        Playthrough playthrough = repository.findById(playthroughUid)
            .orElseThrow(() -> new IllegalArgumentException("Playthrough not found: " + playthroughUid));

        // 1. Playthrough metadata (null-guarded inside the entity)
        if (req.playthrough() != null) {
            UpdatePlaythroughRequest p = req.playthrough();
            playthrough.updateMetadata(
                p.playthroughTypeUid(),
                p.inputUid(),
                p.startedAt(),
                p.playthroughStateUid()
            );
            repository.save(playthrough);
        }

        // 2. Game completions: partial update per completion uid
        if (req.gameCompletions() != null) {
            for (GameCompletionUpdate c : req.gameCompletions()) {
                GameCompletion completion = gameCompletionRepository.findById(c.uid())
                    .orElseThrow(() -> new IllegalArgumentException("GameCompletion not found: " + c.uid()));
                completion.applyPartialUpdate(
                    c.completionStateUid(),
                    c.dlcUid(),
                    c.info(),
                    c.finishedDate(),
                    c.completedDate()
                );
                gameCompletionRepository.save(completion);
            }
        }

        // 3. Link given DLCs to this playthrough
        if (req.dlcUids() != null) {
            for (String dlcUid : req.dlcUids()) {
                Dlc dlc = dlcRepository.findById(dlcUid)
                    .orElseThrow(() -> new IllegalArgumentException("DLC not found: " + dlcUid));
                dlc.setPlaythroughUid(playthroughUid);
                dlcRepository.save(dlc);
            }
        }

        // 4. Backlog entry: partial update
        BacklogUpdate backlogUpdate = req.backlog();
        if (backlogUpdate != null && backlogUpdate.uid() != null) {
            BacklogEntry backlog = backlogRepository.findById(backlogUpdate.uid())
                .orElseThrow(() -> new IllegalArgumentException("Backlog entry not found: " + backlogUpdate.uid()));
            backlog.applyCompletionUpdate(backlogUpdate.removedDate(), backlogUpdate.reason());
            backlogRepository.save(backlog);
        }

        audit.record(
            "Update Playthrough Completion",
            "Playthrough",
            "UPDATE",
            Map.of("playthroughUid", playthroughUid),
            null,
            playthrough,
            req
        );

        return toResponse(playthrough);
    }

    private PlaythroughResponse toResponse(Playthrough pt) {
        return new PlaythroughResponse(
            pt.getUid(),
            pt.getGameInstanceUid(),
            pt.getPlaythroughTypeUid(),
            pt.getInputUid(),
            pt.getStartedAt(),
            pt.getPlaythroughStateUid()
        );
    }
}
