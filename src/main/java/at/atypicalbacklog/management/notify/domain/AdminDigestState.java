package at.atypicalbacklog.management.notify.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "admin_digest_state")
public class AdminDigestState {

    public static final String DIGEST_ID = "daily-admin";

    @Id
    private String id;

    @Column(name = "last_processed_at", nullable = false)
    private Instant lastProcessedAt;

    protected AdminDigestState() {}

    public AdminDigestState(String id, Instant lastProcessedAt) {
        this.id = id;
        this.lastProcessedAt = lastProcessedAt;
    }

    public String getId() { return id; }
    public Instant getLastProcessedAt() { return lastProcessedAt; }

    public void setLastProcessedAt(Instant lastProcessedAt) {
        this.lastProcessedAt = lastProcessedAt;
    }
}
