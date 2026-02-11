package at.atypicalbacklog.management.service.playthrough;

import at.atypicalbacklog.management.audit.AuditRecorder;
import at.atypicalbacklog.management.domain.Playthrough;
import at.atypicalbacklog.management.dto.playthrough.*;
import at.atypicalbacklog.management.repository.PlaythroughRepository;
import at.atypicalbacklog.management.repository.PlaythroughStateRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Year;
import java.util.Map;
import java.util.UUID;

@Service
public class PlaythroughCommandService {

    private final PlaythroughRepository repository;
    private final PlaythroughStateRepository stateRepository;
    private final AuditRecorder audit;

    public PlaythroughCommandService(
        PlaythroughRepository repository,
        PlaythroughStateRepository stateRepository,
        AuditRecorder audit
    ) {
        this.repository = repository;
        this.stateRepository = stateRepository;
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
