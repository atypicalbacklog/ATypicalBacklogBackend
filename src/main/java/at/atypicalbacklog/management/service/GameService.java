package at.atypicalbacklog.management.service;

import at.atypicalbacklog.management.audit.AuditRecorder;
import at.atypicalbacklog.management.domain.Game;
import at.atypicalbacklog.management.dto.CreateGameRequest;
import at.atypicalbacklog.management.dto.GameResponse;
import at.atypicalbacklog.management.repository.GameRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class GameService {

    private final GameRepository repository;
    private final AuditRecorder auditRecorder;

    public GameService(GameRepository repository, AuditRecorder auditRecorder) {
        this.repository = repository;
        this.auditRecorder = auditRecorder;
    }

    public GameResponse create(CreateGameRequest request) {
        Game game = new Game(
            UUID.randomUUID().toString(),
            request.name(),
            request.firstReleaseDate()
        );

        repository.save(game);

        auditRecorder.record(
            "Create Game",
            "Game",
            "CREATE",
            Map.of("gameUid", game.getUid()),
            null,
            game,
            null
        );
        return toResponse(game);
    }

    public GameResponse getById(String uid) {
        Game game = repository.findById(uid)
            .orElseThrow(() -> new IllegalArgumentException("Game not found"));

        return toResponse(game);
    }

    public GameResponse update(String uid, CreateGameRequest request) {
        Game existing = repository.findById(uid)
            .orElseThrow(() -> new IllegalArgumentException("Game not found"));

        Game updated = new Game(
            existing.getUid(),
            request.name(),
            request.firstReleaseDate()
        );

        repository.save(updated);

        auditRecorder.record(
            "Update Game Metadata",
            "Game",
            "UPDATE",
            Map.of("gameUid", uid),
            existing,
            updated,
            null
        );
        
        return toResponse(updated);
    }

    private GameResponse toResponse(Game game) {
        return new GameResponse(
            game.getUid(),
            game.getName(),
            game.getFirstReleaseDate()
        );
    }
}
