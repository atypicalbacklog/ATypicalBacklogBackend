package at.atypicalbacklog.management.audit;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "history_log")
public class HistoryLogEntry {

    @Id
    private String uid;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private String requestId;

    @Column(nullable = false)
    private String intent;

    @Column(nullable = false)
    private String affectedDomain;

    @Column(nullable = false)
    private String changeType;

    @Column(columnDefinition = "jsonb")
    private String targetUids;

    @Column(columnDefinition = "jsonb")
    private String oldState;

    @Column(columnDefinition = "jsonb")
    private String newState;

    @Column(columnDefinition = "jsonb")
    private String changedData;

    protected HistoryLogEntry() {}

    public HistoryLogEntry(
        String uid,
        Instant createdAt,
        String requestId,
        String intent,
        String affectedDomain,
        String changeType,
        String targetUids,
        String oldState,
        String newState,
        String changedData
    ) {
        this.uid = uid;
        this.createdAt = createdAt;
        this.requestId = requestId;
        this.intent = intent;
        this.affectedDomain = affectedDomain;
        this.changeType = changeType;
        this.targetUids = targetUids;
        this.oldState = oldState;
        this.newState = newState;
        this.changedData = changedData;
    }
}
