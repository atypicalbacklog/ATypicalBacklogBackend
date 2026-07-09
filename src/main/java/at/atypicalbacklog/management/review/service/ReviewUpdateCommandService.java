package at.atypicalbacklog.management.review.service;

import at.atypicalbacklog.management.audit.AuditRecorder;
import at.atypicalbacklog.management.review.dto.UpdateReviewRequest;
import at.atypicalbacklog.management.review.domain.Review;
import at.atypicalbacklog.management.review.repository.ReviewRepository;

import org.springframework.stereotype.Service;

import java.util.HashMap;
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

        r.updateDetails(req.reviewDate(), req.tierUid(), req.text(), req.preferredImageUid(), req.presentationType());
        repository.save(r);

        Map<String, Object> changedData = new HashMap<>();
        changedData.put("reviewDate", req.reviewDate());
        changedData.put("tierUid", req.tierUid());
        changedData.put("textChanged", req.text() != null);
        changedData.put("preferredImageUid", req.preferredImageUid());
        changedData.put("presentationType", req.presentationType());

        audit.record(
            "Update Review Details",
            "Review",
            "UPDATE",
            Map.of("reviewUid", r.getUid()),
            null,
            changedData,
            null
        );
    }
}