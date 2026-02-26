package at.atypicalbacklog.management.review.service;

import at.atypicalbacklog.management.audit.AuditRecorder;
import at.atypicalbacklog.management.review.dto.UpdateReviewTierBulkRequest;
import at.atypicalbacklog.management.review.domain.Review;
import at.atypicalbacklog.management.review.repository.ReviewRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class ReviewTierCommandService {

    private final ReviewRepository repository;
    private final AuditRecorder audit;

    public ReviewTierCommandService(
        ReviewRepository repository,
        AuditRecorder audit
    ) {
        this.repository = repository;
        this.audit = audit;
    }

    public void updateTier(UpdateReviewTierBulkRequest req) {

        if (req.reviewUids() != null && !req.reviewUids().isEmpty()) {
            for (String uid : req.reviewUids()) {
                Review r = repository.findById(uid)
                    .orElseThrow();

                String before = r.getTierUid();
                r.updateTier(req.tierUid());
                repository.save(r);

                audit.record(
                    "Update Review Tier",
                    "Review",
                    "UPDATE",
                    Map.of("reviewUid", uid),
                    Map.of("tierUid", before),
                    Map.of("tierUid", req.tierUid()),
                    null
                );
            }
            return;
        }

        for (String ptUid : req.playthroughUids()) {
            Review r = repository.findByPlaythroughUid(ptUid)
                .orElseGet(() -> new Review(
                    UUID.randomUUID().toString(),
                    ptUid,
                    LocalDate.now(),
                    req.tierUid(),
                    null
                ));

            r.updateTier(req.tierUid());
            repository.save(r);

            audit.record(
                "Set Review Tier",
                "Review",
                "CREATE_OR_UPDATE",
                Map.of("playthroughUid", ptUid),
                null,
                Map.of("tierUid", req.tierUid()),
                null
            );
        }
    }
}