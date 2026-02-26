package at.atypicalbacklog.management.hltb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import at.atypicalbacklog.management.hltb.domain.HltbEstimate;

import java.util.Optional;

public interface HltbEstimateRepository extends JpaRepository<HltbEstimate, String> {

    Optional<HltbEstimate> findByGameInstanceUid(String gameInstanceUid);
}
