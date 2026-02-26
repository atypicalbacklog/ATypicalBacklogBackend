package at.atypicalbacklog.management.yearlyreview.service;

import at.atypicalbacklog.management.yearlyreview.dto.UpdateYearlyReviewRequest;
import at.atypicalbacklog.management.yearlyreview.repository.YearlyReviewRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class YearlyReviewCommandService {

    private final YearlyReviewRepository reviewRepo;

    public YearlyReviewCommandService(YearlyReviewRepository reviewRepo) {
        this.reviewRepo = reviewRepo;
    }

    /**
     * Updates introText and outroText for the yearly review with the given UID.
     * Null fields in the request leave the existing value unchanged.
     *
     * @return true if the review was found and updated, false if not found
     */
    @Transactional
    public boolean updateIntroOutro(String uid, UpdateYearlyReviewRequest req) {
        return reviewRepo.findById(uid)
            .map(review -> {
                if (req.introText() != null) {
                    review.setIntroText(req.introText());
                }
                if (req.outroText() != null) {
                    review.setOutroText(req.outroText());
                }
                reviewRepo.save(review);
                return true;
            })
            .orElse(false);
    }
}
