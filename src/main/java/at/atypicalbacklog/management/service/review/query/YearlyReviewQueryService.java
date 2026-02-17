package at.atypicalbacklog.management.service.review.query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import at.atypicalbacklog.management.dto.review.yearly.YearlyReviewDto;
import at.atypicalbacklog.management.repository.YearlyReviewRepository;
import at.atypicalbacklog.management.repository.YearlyReviewCategoryRepository;
import at.atypicalbacklog.management.repository.YearlyReviewGameRepository;
import at.atypicalbacklog.management.domain.review.YearlyReview;
import at.atypicalbacklog.management.domain.review.YearlyReviewCategory;
import at.atypicalbacklog.management.domain.review.YearlyReviewGame;
import at.atypicalbacklog.management.dto.review.yearly.YearlyReviewCategoryDto;
import at.atypicalbacklog.management.dto.review.yearly.YearlyReviewGameDto;

import java.util.List;
import java.util.UUID;

@Service
public class YearlyReviewQueryService {

    private final YearlyReviewRepository reviewRepo;
    private final YearlyReviewCategoryRepository categoryRepo;
    private final YearlyReviewGameRepository gameRepo;

    public YearlyReviewQueryService(YearlyReviewRepository reviewRepo, YearlyReviewCategoryRepository categoryRepo, YearlyReviewGameRepository gameRepo) {
        this.reviewRepo = reviewRepo;
        this.categoryRepo = categoryRepo;
        this.gameRepo = gameRepo;
    }

    @Transactional
    public YearlyReviewDto getOrCreate(int year) {

        List<YearlyReview> reviews =
            reviewRepo.findByYearOrderByValidDesc(year);

        YearlyReview review;

        if (reviews.isEmpty()) {
            review = new YearlyReview(
                UUID.randomUUID().toString(),
                year
            );
            reviewRepo.save(review);
        } else {
            review = reviews.get(0);
        }

        var categories =
            categoryRepo
                .findByYearlyReviewUidAndValidTrueOrderByOrderAsc(review.getUid())
                .stream()
                .map(cat -> new YearlyReviewCategoryDto(
                    cat.getUid(),
                    cat.getName(),
                    cat.getOrder(),
                    gameRepo
                        .findByYearlyReviewCategoryUidAndValidTrueOrderByOrderAsc(cat.getUid())
                        .stream()
                        .map(g -> new YearlyReviewGameDto(
                            g.getUid(),
                            g.getGameUid(),
                            g.getGameInstanceUid(),
                            g.getPreferredImageUid(),
                            g.getText(),
                            g.getOrder()
                        ))
                        .toList()
                ))
                .toList();

        return new YearlyReviewDto(
            review.getUid(),
            review.getYear(),
            review.isValid(),
            categories
        );
    }
}