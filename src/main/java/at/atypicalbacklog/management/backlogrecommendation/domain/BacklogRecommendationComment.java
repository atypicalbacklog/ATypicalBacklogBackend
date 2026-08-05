package at.atypicalbacklog.management.backlogrecommendation.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "backlog_recommendation_comment")
public class BacklogRecommendationComment {

    @Id
    private String uid;

    @Column(name = "creation_date", nullable = false)
    private Instant creationDate;

    @Column(name = "backlog_recommendation_uid", nullable = false)
    private String backlogRecommendationUid;

    @Column(nullable = false, columnDefinition = "text")
    private String comment;

    @Column(name = "moderation_flagged", nullable = false)
    private boolean moderationFlagged;

    @Column(name = "moderation_reason", length = 200)
    private String moderationReason;

    @Column(name = "moderation_score")
    private Double moderationScore;

    @Column(name = "moderation_checked_at")
    private Instant moderationCheckedAt;

    protected BacklogRecommendationComment() {}

    public BacklogRecommendationComment(
        String uid,
        String backlogRecommendationUid,
        String comment
    ) {
        this.uid = uid;
        this.creationDate = Instant.now();
        this.backlogRecommendationUid = backlogRecommendationUid;
        this.comment = comment;
        this.moderationFlagged = false;
        this.moderationReason = null;
        this.moderationScore = null;
        this.moderationCheckedAt = null;
    }

    public void applyModeration(boolean flagged, String reason, Double score) {
        this.moderationFlagged = flagged;
        this.moderationReason = reason;
        this.moderationScore = score;
        this.moderationCheckedAt = Instant.now();
    }

    public String getUid() { return uid; }
    public Instant getCreationDate() { return creationDate; }
    public String getBacklogRecommendationUid() { return backlogRecommendationUid; }
    public String getComment() { return comment; }
    public boolean isModerationFlagged() { return moderationFlagged; }
    public String getModerationReason() { return moderationReason; }
    public Double getModerationScore() { return moderationScore; }
    public Instant getModerationCheckedAt() { return moderationCheckedAt; }
}
