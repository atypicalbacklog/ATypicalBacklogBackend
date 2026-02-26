package at.atypicalbacklog.management.hltb.service;

import at.atypicalbacklog.management.hltb.domain.HltbEstimate;
import at.atypicalbacklog.management.hltb.dto.HltbEstimateResponse;
import at.atypicalbacklog.management.hltb.dto.UpsertHltbEstimateRequest;
import at.atypicalbacklog.management.hltb.repository.HltbEstimateRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class HltbEstimateCommandService {

    private final HltbEstimateRepository repository;

    public HltbEstimateCommandService(HltbEstimateRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public HltbEstimateResponse upsert(UpsertHltbEstimateRequest req) {
        Instant now = Instant.now();
        HltbEstimate entity;

        if (req.uid() != null && !req.uid().isBlank()) {
            entity = repository.findById(req.uid()).orElse(null);
            if (entity != null) {
                entity.setGameInstanceUid(req.gameInstanceUid());
                entity.setMainHours(req.mainHours());
                entity.setExtraHours(req.extraHours());
                entity.setCompleteHours(req.completeHours());
                entity.setExternalId(req.externalId());
                entity.setLastUpdated(now);
                entity = repository.save(entity);
                return toResponse(entity);
            }
        }

        String uid = req.uid() != null && !req.uid().isBlank() ? req.uid() : UUID.randomUUID().toString();
        entity = new HltbEstimate(
            uid,
            req.gameInstanceUid(),
            req.mainHours(),
            req.extraHours(),
            req.completeHours(),
            req.externalId(),
            now
        );
        entity = repository.save(entity);
        return toResponse(entity);
    }

    private static HltbEstimateResponse toResponse(HltbEstimate e) {
        return new HltbEstimateResponse(
            e.getUid(),
            e.getGameInstanceUid(),
            e.getMainHours(),
            e.getExtraHours(),
            e.getCompleteHours(),
            e.getExternalId(),
            e.getLastUpdated()
        );
    }
}
