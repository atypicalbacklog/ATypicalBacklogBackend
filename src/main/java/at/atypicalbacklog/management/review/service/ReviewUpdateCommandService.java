package at.atypicalbacklog.management.review.service;

import at.atypicalbacklog.management.audit.AuditRecorder;
import at.atypicalbacklog.management.review.dto.UpdateReviewRequest;
import at.atypicalbacklog.management.review.domain.Review;
import at.atypicalbacklog.management.review.repository.ReviewRepository;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ReviewUpdateCommandService {

    private final ReviewRepository repository;
    private final AuditRecorder audit;

    public ReviewUpdateCommandService(
        ReviewRepository repository,
        AuditRecorder audit
    ) {
        this.repository = repository;
        this.audit = audit;
    }

    public void update(UpdateReviewRequest req) {

        Review r = repository.findById(req.reviewUid())
            .orElseThrow();

        r.updateDetails(req.reviewDate(), req.tierUid(), req.text());
        repository.save(r);

        audit.record(
            "Update Review Details",
            "Review",
            "UPDATE",
            Map.of("reviewUid", r.getUid()),
            null,
            Map.of(
                "reviewDate", req.reviewDate(),
                "tierUid", req.tierUid(),
                "textChanged", req.text() != null
            ),
            null
        );
    }
}