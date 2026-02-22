package at.atypicalbacklog.management.service.dlc;

import at.atypicalbacklog.management.audit.AuditRecorder;
import at.atypicalbacklog.management.domain.Dlc;
import at.atypicalbacklog.management.domain.GameCompletion;
import at.atypicalbacklog.management.dto.dlc.*;
import at.atypicalbacklog.management.repository.CompletionStateRepository;
import at.atypicalbacklog.management.repository.DlcRepository;
import at.atypicalbacklog.management.repository.GameCompletionRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class DlcCommandService {

    private final DlcRepository repository;
    private final GameCompletionRepository gameCompletionRepo;
    private final CompletionStateRepository completionStateRepo;
    private final AuditRecorder audit;

    public DlcCommandService(
        DlcRepository repository,
        GameCompletionRepository gameCompletionRepo,
        CompletionStateRepository completionStateRepo,
        AuditRecorder audit
    ) {
        this.repository = repository;
        this.gameCompletionRepo = gameCompletionRepo;
        this.completionStateRepo = completionStateRepo;
        this.audit = audit;
    }

    /** Create DLC */
    public DlcResponse create(CreateDlcRequest req) {
        if (req.releaseDate() == null) {
            throw new IllegalArgumentException("releaseDate is required on creation");
        }

        Dlc dlc = new Dlc(
            UUID.randomUUID().toString(),
            req.gameInstanceUid(),
            req.name(),
            req.receivedDate(),
            req.releaseDate(),
            req.playthroughUid()
        );

        repository.save(dlc);

        String completionStateUid = req.completionStateUid();
        if (completionStateUid == null || completionStateUid.isBlank()) {
            completionStateUid = completionStateRepo.findByNameIgnoreCase("None")
                .orElseThrow(() -> new IllegalStateException("CompletionState 'None' not found"))
                .getUid();
        }

        GameCompletion gc = new GameCompletion(
            UUID.randomUUID().toString(),
            dlc.getGameInstanceUid(),
            completionStateUid,
            dlc.getUid(),
            req.info(),
            req.finishedDate(),
            req.completedDate()
        );
        gameCompletionRepo.save(gc);

        Map<String, Object> auditAttrs = new HashMap<>();
        auditAttrs.put("dlcUid", dlc.getUid());
        auditAttrs.put("gameInstanceUid", dlc.getGameInstanceUid());
        if (dlc.getPlaythroughUid() != null) {
            auditAttrs.put("playthroughUid", dlc.getPlaythroughUid());
        }
        audit.record(
            "Create DLC",
            "DLC",
            "CREATE",
            auditAttrs,
            null,
            dlc,
            null
        );

        return toResponse(dlc, completionStateUid, gc.getFinishedDate(), gc.getCompletedDate());
    }

    /** Update DLC */
    public DlcResponse update(String uid, UpdateDlcRequest req) {

        Dlc dlc = repository.findById(uid)
            .orElseThrow(() -> new IllegalArgumentException("DLC not found"));

        dlc.update(req.name(), req.receivedDate(), req.releaseDate());

        String newPlaythroughUid = req.playthroughUid();
        boolean hasNewPlaythrough = newPlaythroughUid != null && !newPlaythroughUid.isBlank();
        if (hasNewPlaythrough) {
            dlc.setPlaythroughUid(Objects.requireNonNull(newPlaythroughUid));
        } else if (Boolean.TRUE.equals(req.removePreviousPlaythrough())) {
            dlc.setPlaythroughUid(null);
        }

        repository.save(dlc);

        gameCompletionRepo.findByDlcUid(uid).ifPresent(gc -> {
            String state = req.completionStateUid() != null ? req.completionStateUid() : gc.getCompletionStateUid();
            String info = gc.getInfo();
            if (req.completionStateUid() != null || req.finishedDate() != null || req.completedDate() != null) {
                gc.update(state, info, req.finishedDate(), req.completedDate());
                gameCompletionRepo.save(gc);
            }
        });

        audit.record(
            "Update DLC",
            "DLC",
            "UPDATE",
            Map.of("dlcUid", uid),
            null,
            dlc,
            req
        );

        var gc = gameCompletionRepo.findByDlcUid(uid).orElse(null);
        String completionStateUid = gc != null ? gc.getCompletionStateUid() : null;
        java.time.LocalDate finishedDate = gc != null ? gc.getFinishedDate() : null;
        java.time.LocalDate completedDate = gc != null ? gc.getCompletedDate() : null;
        return toResponse(dlc, completionStateUid, finishedDate, completedDate);
    }

    private DlcResponse toResponse(Dlc dlc, String completionStateUid,
                                   java.time.LocalDate finishedDate, java.time.LocalDate completedDate) {
        return new DlcResponse(
            dlc.getUid(),
            dlc.getGameInstanceUid(),
            dlc.getName(),
            dlc.getReceivedDate(),
            dlc.getReleaseDate(),
            dlc.getPlaythroughUid(),
            completionStateUid,
            null,
            finishedDate,
            completedDate
        );
    }
}
