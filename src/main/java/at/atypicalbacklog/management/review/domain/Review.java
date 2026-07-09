package at.atypicalbacklog.management.review.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "review")
public class Review {

    @Id
    private String uid;

    @Column(name = "creation_date", nullable = false)
    private Instant creationDate;

    @Column(name = "playthrough_uid", nullable = false, unique = true)
    private String playthroughUid;

    @Column(name = "review_date")
    private LocalDate reviewDate;

    @Column(name = "tier_uid")
    private String tierUid;

    @Column(columnDefinition = "text")
    private String text;

    @Column(name = "preferred_image_uid")
    private String preferredImageUid;

    @Column(name = "presentation_type", nullable = false, length = 100)
    private String presentationType;

    protected Review() {}

    public Review(
        String uid,
        String playthroughUid,
        LocalDate reviewDate,
        String tierUid,
        String text
    ) {
        this.uid = uid;
        this.creationDate = Instant.now();
        this.playthroughUid = playthroughUid;
        this.reviewDate = reviewDate;
        this.tierUid = tierUid;
        this.text = text;
        this.presentationType = "DEFAULT";
    }

    public void updateTier(String tierUid) {
        this.tierUid = tierUid;
    }

    public void updateDetails(
        LocalDate reviewDate,
        String tierUid,
        String text,
        String preferredImageUid,
        String presentationType
    ) {
        if (reviewDate != null) this.reviewDate = reviewDate;
        if (tierUid != null) this.tierUid = tierUid;
        if (text != null) this.text = text;
        if (preferredImageUid != null) this.preferredImageUid = preferredImageUid;
        if (presentationType != null) this.presentationType = presentationType;
    }

    // getters only
    public String getUid() {
        return uid;
    }

    public String getTierUid() {
        return tierUid;
    }

    public String getPreferredImageUid() {
        return preferredImageUid;
    }

    public String getPresentationType() {
        return presentationType;
    }
}