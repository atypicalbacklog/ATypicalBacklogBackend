package at.atypicalbacklog.management.backlogrecommendation.repository;

import at.atypicalbacklog.management.backlogrecommendation.domain.BacklogRecommendationComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BacklogRecommendationCommentRepository
    extends JpaRepository<BacklogRecommendationComment, String> {

    List<BacklogRecommendationComment> findByBacklogRecommendationUidOrderByCreationDateAsc(
        String backlogRecommendationUid
    );
}
