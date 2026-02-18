package at.atypicalbacklog.management.repository;

import at.atypicalbacklog.management.domain.HltbEstimate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HltbEstimateRepository extends JpaRepository<HltbEstimate, String> {

    Optional<HltbEstimate> findByGameInstanceUid(String gameInstanceUid);
}
