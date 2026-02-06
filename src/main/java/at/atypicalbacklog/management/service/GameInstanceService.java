package at.atypicalbacklog.management.service;

import at.atypicalbacklog.management.audit.AuditRecorder;
import at.atypicalbacklog.management.domain.GameInstance;
import at.atypicalbacklog.management.dto.*;
import at.atypicalbacklog.management.repository.GameInstanceRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class GameInstanceService {

    private final GameInstanceRepository repository;
    private final AuditRecorder auditRecorder;

    public GameInstanceService(GameInstanceRepository repository, AuditRecorder auditRecorder) {
        this.auditRecorder = auditRecorder;
        this.repository = repository;
    }

    public GameInstanceResponse create(CreateGameInstanceRequest req) {
        GameInstance instance = new GameInstance(
            UUID.randomUUID().toString(),
            Instant.now(),
            req.name(),
            req.releaseDate(),
            req.gameUid(),
            req.platformUid(),
            req.originalConsoleUid(),
            req.variantUid(),
            req.receivedDate(),
            Boolean.TRUE.equals(req.forFree()),
            req.inPossession() == null || req.inPossession()
        );

        repository.save(instance);
        auditRecorder.record(
            "Create Game Instance",
            "GameInstance",
            "CREATE",
            Map.of(
                "gameInstanceUid", instance.getUid(),
                "gameUid", instance.getGameUid()
            ),
            null,
            instance,
            null
        );
        
        return toResponse(instance);
    }

    public GameInstanceResponse update(String uid, UpdateGameInstanceRequest req) {
        Objects.requireNonNull(uid, "uid");
        GameInstance existing = repository.findById(uid)
            .orElseThrow(() -> new IllegalArgumentException("GameInstance not found"));

        GameInstance updated = new GameInstance(
            existing.getUid(),
            existing.getCreationDate(),
            req.name() != null ? req.name() : existing.getName(),
            req.releaseDate() != null ? req.releaseDate() : existing.getReleaseDate(),
            req.gameUid() != null ? req.gameUid() : existing.getGameUid(),
            req.platformUid() != null ? req.platformUid() : existing.getPlatformUid(),
            req.originalConsoleUid() != null ? req.originalConsoleUid() : existing.getOriginalConsoleUid(),
            req.variantUid() != null ? req.variantUid() : existing.getVariantUid(),
            req.receivedDate() != null ? req.receivedDate() : existing.getReceivedDate(),
            req.forFree() != null ? req.forFree() : existing.isForFree(),
            req.inPossession() != null ? req.inPossession() : existing.isInPossession()
        );

        repository.save(updated);
        Map<String, Object> changedData = new HashMap<>();
        changedData.put("gameInstanceUid", existing.getUid());
        if (req.gameUid() != null) changedData.put("gameUid", req.gameUid());
        if (req.name() != null) changedData.put("name", req.name());
        if (req.platformUid() != null) changedData.put("platformUid", req.platformUid());
        if (req.variantUid() != null) changedData.put("variantUid", req.variantUid());
        if (req.inPossession() != null) changedData.put("inPossession", req.inPossession());
        auditRecorder.record(
            "Update Game Instance Metadata",
            "GameInstance",
            "UPDATE",
            Map.of("gameInstanceUid", existing.getUid()),
            existing,
            updated,
            changedData
        );
        
        return toResponse(updated);
    }

    public GameInstanceResponse getById(String uid) {
        Objects.requireNonNull(uid, "uid");
        return repository.findById(uid)
            .map(this::toResponse)
            .orElseThrow(() -> new IllegalArgumentException("GameInstance not found"));
    }

    private GameInstanceResponse toResponse(GameInstance gi) {
        return new GameInstanceResponse(
            gi.getUid(),
            gi.getGameUid(),
            gi.getPlatformUid(),
            gi.getVariantUid(),
            gi.getName(),
            gi.getReleaseDate(),
            gi.getOriginalConsoleUid(),
            gi.getReceivedDate(),
            gi.isForFree(),
            gi.isInPossession()
        );
    }
}
