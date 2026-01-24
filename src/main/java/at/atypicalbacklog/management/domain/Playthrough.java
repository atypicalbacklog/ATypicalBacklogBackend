package at.atypicalbacklog.management.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "playthrough")
public class Playthrough {

    @Id
    private String uid;

    @Column(nullable = false)
    private Instant creationDate;

    @Column(nullable = false)
    private String gameInstanceUid;

    @Column(nullable = false)
    private String playthroughStateUid;

    @Column(nullable = false)
    private int year;

    protected Playthrough() {}

    public Playthrough(String uid, String gameInstanceUid, String stateUid, int year) {
        this.uid = uid;
        this.creationDate = Instant.now();
        this.gameInstanceUid = gameInstanceUid;
        this.playthroughStateUid = stateUid;
        this.year = year;
    }

    public void changeState(String stateUid) {
        this.playthroughStateUid = stateUid;
    }

    // getters
}
