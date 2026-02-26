package at.atypicalbacklog.management.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import at.atypicalbacklog.management.review.domain.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, String> {

    Optional<Review> findByPlaythroughUid(String playthroughUid);

    List<Review> findByPlaythroughUidIn(List<String> playthroughUids);
}