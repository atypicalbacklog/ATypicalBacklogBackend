package at.atypicalbacklog.management.yearlyreview.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import at.atypicalbacklog.management.yearlyreview.domain.YearlyReview;

import java.util.List;

public interface YearlyReviewRepository
        extends JpaRepository<YearlyReview, String> {

    List<YearlyReview> findByYearOrderByValidDesc(int year);
}