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
    }

    public String getUid() { return uid; }
    public Instant getCreationDate() { return creationDate; }
    public String getBacklogRecommendationUid() { return backlogRecommendationUid; }
    public String getComment() { return comment; }
}
