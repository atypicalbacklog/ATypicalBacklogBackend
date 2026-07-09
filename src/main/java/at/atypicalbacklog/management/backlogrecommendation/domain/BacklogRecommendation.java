package at.atypicalbacklog.management.backlogrecommendation.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "backlog_recommendation")
public class BacklogRecommendation {

    @Id
    private String uid;

    @Column(name = "creation_date", nullable = false)
    private Instant creationDate;

    @Column(name = "game_recommendation_uid", nullable = false)
    private String gameRecommendationUid;

    @Column(name = "game_uid")
    private String gameUid;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    private BacklogRecommendationState state;

    @Column(name = "state_reason")
    private String stateReason;

    @Column(name = "state_date", nullable = false)
    private LocalDate stateDate;

    @Column(name = "votecount", nullable = false)
    private int voteCount;

    @Column(name = "original_recommendation_by")
    private String originalRecommendationBy;

    protected BacklogRecommendation() {}

    public BacklogRecommendation(
        String uid,
        String gameRecommendationUid,
        String gameUid,
        String name,
        String originalRecommendationBy
    ) {
        this.uid = uid;
        this.creationDate = Instant.now();
        this.gameRecommendationUid = gameRecommendationUid;
        this.gameUid = gameUid;
        this.name = name;
        this.state = BacklogRecommendationState.NEW;
        this.stateReason = null;
        this.stateDate = LocalDate.now();
        this.voteCount = 1;
        this.originalRecommendationBy = originalRecommendationBy;
    }

    public void upvote() {
        this.voteCount++;
    }

    public String getUid() { return uid; }
    public Instant getCreationDate() { return creationDate; }
    public String getGameRecommendationUid() { return gameRecommendationUid; }
    public String getGameUid() { return gameUid; }
    public String getName() { return name; }
    public BacklogRecommendationState getState() { return state; }
    public String getStateReason() { return stateReason; }
    public LocalDate getStateDate() { return stateDate; }
    public int getVoteCount() { return voteCount; }
    public String getOriginalRecommendationBy() { return originalRecommendationBy; }
}
