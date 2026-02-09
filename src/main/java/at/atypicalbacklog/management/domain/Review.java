package at.atypicalbacklog.management.domain;

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
    }

    public void updateTier(String tierUid) {
        this.tierUid = tierUid;
    }

    public void updateDetails(LocalDate reviewDate, String tierUid, String text) {
        if (reviewDate != null) this.reviewDate = reviewDate;
        if (tierUid != null) this.tierUid = tierUid;
        if (text != null) this.text = text;
    }

    // getters only
    public String getUid() {
        return uid;
    }

    public String getTierUid() {
        return tierUid;
    }
}