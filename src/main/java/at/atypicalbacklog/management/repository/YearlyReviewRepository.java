package at.atypicalbacklog.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import at.atypicalbacklog.management.domain.review.YearlyReview;

import java.util.List;

public interface YearlyReviewRepository
        extends JpaRepository<YearlyReview, String> {

    List<YearlyReview> findByYearOrderByValidDesc(int year);
}