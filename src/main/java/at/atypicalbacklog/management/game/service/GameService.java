package at.atypicalbacklog.management.game.service;

import at.atypicalbacklog.management.audit.AuditRecorder;
import at.atypicalbacklog.management.game.domain.Game;
import at.atypicalbacklog.management.game.dto.CreateGameRequest;
import at.atypicalbacklog.management.game.dto.GameResponse;
import at.atypicalbacklog.management.game.repository.GameRepository;
import at.atypicalbacklog.management.image.domain.ImageOwnerType;
import at.atypicalbacklog.management.image.domain.ImageType;
import at.atypicalbacklog.management.image.repository.GameImageRepository;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class GameService {

    private final GameRepository repository;
    private final GameImageRepository imageRepository;
    private final AuditRecorder auditRecorder;

    public GameService(GameRepository repository, GameImageRepository imageRepository, AuditRecorder auditRecorder) {
        this.repository = repository;
        this.imageRepository = imageRepository;
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
        Objects.requireNonNull(uid, "uid");
        Game game = repository.findById(uid)
            .orElseThrow(() -> new IllegalArgumentException("Game not found"));

        return toResponse(game);
    }

    public GameResponse update(String uid, CreateGameRequest request) {
        Objects.requireNonNull(uid, "uid");
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
        String coverImageUid = resolveGameCoverImageUid(game.getUid());
        return new GameResponse(
            game.getUid(),
            game.getName(),
            game.getFirstReleaseDate(),
            coverImageUid
        );
    }

    /**
     * Resolve cover image for game: primary first, else any first cover.
     */
    String resolveGameCoverImageUid(String gameUid) {
        var covers = imageRepository.findByOwnerTypeAndTypeAndGameUid(
            ImageOwnerType.GAME, ImageType.COVER, gameUid);
        return covers.stream()
            .filter(img -> img.isPrimaryImage())
            .findFirst()
            .or(() -> covers.stream().findFirst())
            .map(img -> img.getUid())
            .orElse(null);
    }
}
