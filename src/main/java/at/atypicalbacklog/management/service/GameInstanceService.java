package at.atypicalbacklog.management.service;

import at.atypicalbacklog.management.domain.GameInstance;
import at.atypicalbacklog.management.dto.CreateGameInstanceRequest;
import at.atypicalbacklog.management.dto.GameInstanceResponse;
import at.atypicalbacklog.management.repository.GameInstanceRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GameInstanceService {

    private final GameInstanceRepository repository;

    public GameInstanceService(GameInstanceRepository repository) {
        this.repository = repository;
    }

    public GameInstanceResponse create(CreateGameInstanceRequest request) {
        GameInstance instance = new GameInstance(
            UUID.randomUUID().toString(),
            request.gameUid(),
            request.platformUid(),
            request.variantUid(),
            request.receivedDate()
        );

        repository.save(instance);
        return toResponse(instance);
    }

    public GameInstanceResponse getById(String uid) {
        GameInstance instance = repository.findById(uid)
            .orElseThrow(() -> new IllegalArgumentException("GameInstance not found"));

        return toResponse(instance);
    }

    public GameInstanceResponse update(String uid, CreateGameInstanceRequest request) {
        GameInstance existing = repository.findById(uid)
            .orElseThrow(() -> new IllegalArgumentException("GameInstance not found"));

        GameInstance updated = new GameInstance(
            existing.getUid(),
            request.gameUid(),
            request.platformUid(),
            request.variantUid(),
            request.receivedDate()
        );

        repository.save(updated);
        return toResponse(updated);
    }

    private GameInstanceResponse toResponse(GameInstance instance) {
        return new GameInstanceResponse(
            instance.getUid(),
            instance.getGameUid(),
            instance.getPlatformUid(),
            instance.getVariantUid()
        );
    }
}

