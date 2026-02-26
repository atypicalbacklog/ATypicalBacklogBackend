package at.atypicalbacklog.management.backlog.service;

import at.atypicalbacklog.management.audit.AuditRecorder;
import at.atypicalbacklog.management.backlog.domain.BacklogEntry;
import at.atypicalbacklog.management.backlog.dto.AddGameToBacklogRequest;
import at.atypicalbacklog.management.backlog.dto.RemoveGameFromBacklogRequest;
import at.atypicalbacklog.management.backlog.dto.UpdateBacklogReasonRequest;
import at.atypicalbacklog.management.backlog.repository.BacklogRepository;


import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class BacklogCommandService {

    private final BacklogRepository repository;
    private final AuditRecorder audit;

    public BacklogCommandService(
        BacklogRepository repository,
        AuditRecorder audit
    ) {
        this.repository = repository;
        this.audit = audit;
    }

    /** AddGameToBacklog */
    public void add(AddGameToBacklogRequest req) {

        repository.findFirstByGameUidAndRemovedDateIsNull(req.gameUid())
            .ifPresent(b -> {
                throw new IllegalStateException("Game already in backlog");
            });

        BacklogEntry entry = new BacklogEntry(
            UUID.randomUUID().toString(),
            req.gameUid(),
            req.addedDate(),
            req.reason()
        );

        repository.save(entry);

        audit.record(
            "Add Game To Backlog",
            "Backlog",
            "CREATE",
            Map.of("gameUid", req.gameUid()),
            null,
            entry,
            null
        );
    }

    /** RemoveGameFromBacklog */
    public void remove(String gameUid, RemoveGameFromBacklogRequest req) {

        BacklogEntry active = repository.findFirstByGameUidAndRemovedDateIsNull(gameUid)
            .orElseThrow(() -> new IllegalStateException("Game not in backlog"));

        BacklogEntry before = active;
        active.remove(req.removedDate());
        repository.save(active);

        audit.record(
            "Remove Game From Backlog",
            "Backlog",
            "STATE_CHANGE",
            Map.of("gameUid", gameUid),
            before,
            active,
            Map.of("removedDate", req.removedDate())
        );
    }

    /** UpdateBacklogReason */
    public void updateReason(String uid, UpdateBacklogReasonRequest req) {

        BacklogEntry entry = repository.findById(uid)
            .orElseThrow(() -> new IllegalArgumentException("Backlog entry not found"));

        String beforeReason = entry.getReason();
        entry.updateReason(req.reason());
        repository.save(entry);

        audit.record(
            "Update Backlog Reason",
            "Backlog",
            "UPDATE",
            Map.of("backlogUid", uid),
            Map.of("reason", beforeReason),
            Map.of("reason", req.reason()),
            null
        );
    }
}
