package at.atypicalbacklog.management.service.dlc;

import at.atypicalbacklog.management.audit.AuditRecorder;
import at.atypicalbacklog.management.domain.Dlc;
import at.atypicalbacklog.management.domain.GameCompletion;
import at.atypicalbacklog.management.dto.dlc.*;
import at.atypicalbacklog.management.repository.CompletionStateRepository;
import at.atypicalbacklog.management.repository.DlcRepository;
import at.atypicalbacklog.management.repository.GameCompletionRepository;
import org.springframework.stereotype.Service;

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
            req.info()
        );
        gameCompletionRepo.save(gc);

        audit.record(
            "Create DLC",
            "DLC",
            "CREATE",
            Map.of(
                "dlcUid", dlc.getUid(),
                "gameInstanceUid", dlc.getGameInstanceUid(),
                "playthroughUid", dlc.getPlaythroughUid()
            ),
            null,
            dlc,
            null
        );

        return toResponse(dlc, completionStateUid);
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

        if (req.completionStateUid() != null) {
            gameCompletionRepo.findByDlcUid(uid).ifPresent(gc -> {
                gc.update(req.completionStateUid(), gc.getInfo());
                gameCompletionRepo.save(gc);
            });
        }

        audit.record(
            "Update DLC",
            "DLC",
            "UPDATE",
            Map.of("dlcUid", uid),
            null,
            dlc,
            req
        );

        String completionStateUid = gameCompletionRepo.findByDlcUid(uid)
            .map(gc -> gc.getCompletionStateUid())
            .orElse(null);
        return toResponse(dlc, completionStateUid);
    }

    private DlcResponse toResponse(Dlc dlc, String completionStateUid) {
        return new DlcResponse(
            dlc.getUid(),
            dlc.getGameInstanceUid(),
            dlc.getName(),
            dlc.getReceivedDate(),
            dlc.getReleaseDate(),
            dlc.getPlaythroughUid(),
            completionStateUid
        );
    }
}
