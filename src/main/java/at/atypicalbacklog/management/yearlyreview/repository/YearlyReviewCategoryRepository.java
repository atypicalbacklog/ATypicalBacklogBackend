package at.atypicalbacklog.management.yearlyreview.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import at.atypicalbacklog.management.yearlyreview.domain.YearlyReviewCategory;

import java.util.List;

public interface YearlyReviewCategoryRepository
        extends JpaRepository<YearlyReviewCategory, String> {

    List<YearlyReviewCategory> findByYearlyReviewUidOrderByOrderAsc(String reviewUid);

    /** Fetches only valid categories (valid = true). */
    List<YearlyReviewCategory> findByYearlyReviewUidAndValidTrueOrderByOrderAsc(String reviewUid);
}