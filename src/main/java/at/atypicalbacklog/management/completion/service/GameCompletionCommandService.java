package at.atypicalbacklog.management.completion.service;

import at.atypicalbacklog.management.audit.AuditRecorder;
import at.atypicalbacklog.management.completion.domain.GameCompletion;
import at.atypicalbacklog.management.completion.dto.UpdateGameCompletionRequest;
import at.atypicalbacklog.management.completion.repository.GameCompletionRepository;

import org.springframework.stereotype.Service;

import java.util.HashMap;
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
                req.info(),
                req.finishedDate(),
                req.completedDate()
            ));

        boolean isNew = completion.getUid() == null;
        GameCompletion before = isNew ? null : completion;

        completion.update(req.completionStateUid(), req.info(), req.finishedDate(), req.completedDate());
        repository.save(completion);

        Map<String, Object> changedData = new HashMap<>();
        changedData.put("completionStateUid", req.completionStateUid());
        changedData.put("info", req.info());
        changedData.put("finishedDate", req.finishedDate());
        changedData.put("completedDate", req.completedDate());
        audit.record(
            "Update Game Completion",
            "GameCompletion",
            isNew ? "CREATE" : "UPDATE",
            Map.of("gameInstanceUid", req.gameInstanceUid()),
            before,
            completion,
            changedData
        );
    }
}
