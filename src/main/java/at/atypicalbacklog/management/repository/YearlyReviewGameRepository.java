package at.atypicalbacklog.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import at.atypicalbacklog.management.domain.review.YearlyReviewGame;

import java.util.List;

public interface YearlyReviewGameRepository
        extends JpaRepository<YearlyReviewGame, String> {

    List<YearlyReviewGame> findByYearlyReviewCategoryUidOrderByOrderAsc(String yearlyReviewCategoryUid);

    /** Fetches only valid games (valid = true). */
    List<YearlyReviewGame> findByYearlyReviewCategoryUidAndValidTrueOrderByOrderAsc(String yearlyReviewCategoryUid);
}