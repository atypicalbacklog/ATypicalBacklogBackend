package at.atypicalbacklog.management.backlogrecommendation.repository;

import at.atypicalbacklog.management.backlogrecommendation.domain.GameRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRecommendationRepository extends JpaRepository<GameRecommendation, String> {
}
