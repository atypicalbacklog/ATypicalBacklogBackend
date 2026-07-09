package at.atypicalbacklog.management.backlogrecommendation.repository;

import at.atypicalbacklog.management.backlogrecommendation.domain.BacklogRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BacklogRecommendationRepository extends JpaRepository<BacklogRecommendation, String> {
}
