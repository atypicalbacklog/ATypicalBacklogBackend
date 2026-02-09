package at.atypicalbacklog.management.repository;

import at.atypicalbacklog.management.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, String> {

    Optional<Review> findByPlaythroughUid(String playthroughUid);

    List<Review> findByPlaythroughUidIn(List<String> playthroughUids);
}