package at.atypicalbacklog.management.service;

import at.atypicalbacklog.management.domain.Game;
import at.atypicalbacklog.management.dto.CreateGameRequest;
import at.atypicalbacklog.management.dto.GameResponse;
import at.atypicalbacklog.management.repository.GameRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GameService {

    private final GameRepository repository;

    public GameService(GameRepository repository) {
        this.repository = repository;
    }

    public GameResponse create(CreateGameRequest request) {
        Game game = new Game(
            UUID.randomUUID().toString(),
            request.name(),
            request.firstReleaseDate()
        );

        repository.save(game);
        return toResponse(game);
    }

    public GameResponse getById(String uid) {
        Game game = repository.findById(uid)
            .orElseThrow(() -> new IllegalArgumentException("Game not found"));

        return toResponse(game);
    }

    public GameResponse update(String uid, CreateGameRequest request) {
        Game game = repository.findById(uid)
            .orElseThrow(() -> new IllegalArgumentException("Game not found"));

        Game updated = new Game(
            game.getUid(),
            request.name(),
            request.firstReleaseDate()
        );

        repository.save(updated);
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
