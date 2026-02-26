package at.atypicalbacklog.management.playthrough.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "playthrough")
public class Playthrough {

    @Id
    private String uid;

    @Column(name = "creation_date", nullable = false)
    private Instant creationDate;

    @Column(name = "game_instance_uid", nullable = false)
    private String gameInstanceUid;

    @Column(name = "started_at")
    private LocalDate startedAt;

    @Column(nullable = false)
    private int year;

    @Column(name = "playthrough_type_uid")
    private String playthroughTypeUid;

    @Column(name = "input_uid")
    private String inputUid;

    @Column(name = "playthrough_state_uid", nullable = false)
    private String playthroughStateUid;

    protected Playthrough() {}

    public Playthrough(
        String uid,
        String gameInstanceUid,
        LocalDate startedAt,
        int year,
        String playthroughStateUid
    ) {
        this.uid = uid;
        this.creationDate = Instant.now();
        this.gameInstanceUid = gameInstanceUid;
        this.startedAt = startedAt;
        this.year = year;
        this.playthroughStateUid = playthroughStateUid;
    }

    public void updateMetadata(
        String playthroughTypeUid,
        String inputUid,
        LocalDate startedAt,
        String playthroughStateUid
    ) {
        if (playthroughTypeUid != null) this.playthroughTypeUid = playthroughTypeUid;
        if (inputUid != null) this.inputUid = inputUid;
        if (startedAt != null) this.startedAt = startedAt;
        if (playthroughStateUid != null) this.playthroughStateUid = playthroughStateUid;
    }

    // getters only
    public String getUid() {
        return uid;
    }

    public String getGameInstanceUid() {
        return gameInstanceUid;
    }

    public LocalDate getStartedAt() {
        return startedAt;
    }

    public String getPlaythroughTypeUid() {
        return playthroughTypeUid;
    }

    public String getInputUid() {
        return inputUid;
    }

    public String getPlaythroughStateUid() {
        return playthroughStateUid;
    }
}