package at.atypicalbacklog.management.service.dlc;

import at.atypicalbacklog.management.audit.AuditRecorder;
import at.atypicalbacklog.management.domain.Dlc;
import at.atypicalbacklog.management.dto.dlc.*;
import at.atypicalbacklog.management.repository.DlcRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class DlcCommandService {

    private final DlcRepository repository;
    private final AuditRecorder audit;

    public DlcCommandService(
        DlcRepository repository,
        AuditRecorder audit
    ) {
        this.repository = repository;
        this.audit = audit;
    }

    /** Create DLC */
    public DlcResponse create(CreateDlcRequest req) {

        Dlc dlc = new Dlc(
            UUID.randomUUID().toString(),
            req.gameInstanceUid(),
            req.name(),
            req.receivedDate(),
            req.playthroughUid()
        );

        repository.save(dlc);

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

        return toResponse(dlc);
    }

    private DlcResponse toResponse(Dlc dlc) {
        return new DlcResponse(
            dlc.getUid(),
            dlc.getGameInstanceUid(),
            dlc.getName(),
            dlc.getReceivedDate(),
            dlc.getPlaythroughUid()
        );
    }
}
