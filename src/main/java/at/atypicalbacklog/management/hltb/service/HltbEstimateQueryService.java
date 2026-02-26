package at.atypicalbacklog.management.hltb.service;

import at.atypicalbacklog.management.hltb.domain.HltbEstimate;
import at.atypicalbacklog.management.hltb.dto.HltbEstimateResponse;
import at.atypicalbacklog.management.hltb.repository.HltbEstimateRepository;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class HltbEstimateQueryService {

    private final HltbEstimateRepository repository;

    public HltbEstimateQueryService(HltbEstimateRepository repository) {
        this.repository = repository;
    }

    public Optional<HltbEstimateResponse> getByGameInstanceUid(String gameInstanceUid) {
        return repository.findByGameInstanceUid(gameInstanceUid)
            .map(this::toResponse);
    }

    private HltbEstimateResponse toResponse(HltbEstimate e) {
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
