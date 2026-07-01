package at.atypicalbacklog.management.backlog.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "backlog")
public class BacklogEntry {

    @Id
    private String uid;

    @Column(name = "creation_date", nullable = false)
    private Instant creationDate;

    @Column(nullable = false)
    private String name;

    @Column(name = "added_date", nullable = false)
    private LocalDate addedDate;

    @Column(name = "removed_date")
    private LocalDate removedDate;

    @Column(name = "game_uid", nullable = false)
    private String gameUid;

    @Column(name = "playthrough_uid")
    private String playthroughUid;

    private String reason;

    protected BacklogEntry() {}

    public BacklogEntry(
        String uid,
        String gameUid,
        LocalDate addedDate,
        String reason
    ) {
        this.uid = uid;
        this.creationDate = Instant.now();
        this.gameUid = gameUid;
        this.addedDate = addedDate;
        this.reason = reason;
        this.name = "Backlog Entry";
    }

    public void remove(LocalDate removedDate) {
        this.removedDate = removedDate;
    }

    public void updateReason(String reason) {
        this.reason = reason;
    }

    /** Partial update: only non-null values are applied. */
    public void applyCompletionUpdate(LocalDate removedDate, String reason) {
        if (removedDate != null) this.removedDate = removedDate;
        if (reason != null) this.reason = reason;
    }

    // getters only
    public String getUid() {
        return uid;
    }

    public String getGameUid() {
        return gameUid;
    }

    public LocalDate getAddedDate() {
        return addedDate;
    }

    public LocalDate getRemovedDate() {
        return removedDate;
    }

    public String getReason() {
        return reason;
    }
}
