package at.atypicalbacklog.management.service.playthrough.query;

import at.atypicalbacklog.management.domain.Playthrough;
import at.atypicalbacklog.management.dto.playthrough.PlaythroughResponse;
import at.atypicalbacklog.management.repository.PlaythroughRepository;
import org.springframework.stereotype.Service;

@Service
public class PlaythroughQueryService {

    private final PlaythroughRepository repository;

    public PlaythroughQueryService(PlaythroughRepository repository) {
        this.repository = repository;
    }

    public PlaythroughResponse getByUid(String uid) {
        Playthrough pt = repository.findById(uid)
            .orElseThrow(() -> new IllegalArgumentException("Playthrough not found"));

        return toResponse(pt);
    }

    private PlaythroughResponse toResponse(Playthrough pt) {
        return new PlaythroughResponse(
            pt.getUid(),
            pt.getGameInstanceUid(),
            pt.getPlaythroughTypeUid(),
            pt.getInputUid(),
            pt.getStartedAt(),
            pt.getPlaythroughStateUid()
        );
    }
}
