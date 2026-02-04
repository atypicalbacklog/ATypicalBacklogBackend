package at.atypicalbacklog.management.service.completion;

import at.atypicalbacklog.management.audit.AuditRecorder;
import at.atypicalbacklog.management.domain.GameCompletion;
import at.atypicalbacklog.management.dto.completion.UpdateGameCompletionRequest;
import at.atypicalbacklog.management.repository.GameCompletionRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class GameCompletionCommandService {

    private final GameCompletionRepository repository;
    private final AuditRecorder audit;

    public GameCompletionCommandService(
        GameCompletionRepository repository,
        AuditRecorder audit
    ) {
        this.repository = repository;
        this.audit = audit;
    }

    /** UpdateGameCompletion (create or update) */
    public void update(UpdateGameCompletionRequest req) {

        GameCompletion completion = repository
            .findFirstByGameInstanceUidAndDlcUidIsNull(req.gameInstanceUid())
            .orElseGet(() -> new GameCompletion(
                UUID.randomUUID().toString(),
                req.gameInstanceUid(),
                req.completionStateUid(),
                req.dlcUid(),
                req.info()
            ));

        boolean isNew = completion.getUid() == null;
        GameCompletion before = isNew ? null : completion;

        completion.update(req.completionStateUid(), req.info());
        repository.save(completion);

        audit.record(
            "Update Game Completion",
            "GameCompletion",
            isNew ? "CREATE" : "UPDATE",
            Map.of("gameInstanceUid", req.gameInstanceUid()),
            before,
            completion,
            Map.of(
                "completionStateUid", req.completionStateUid(),
                "info", req.info()
            )
        );
    }
}
