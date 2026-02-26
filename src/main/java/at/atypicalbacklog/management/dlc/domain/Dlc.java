package at.atypicalbacklog.management.dlc.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "dlc")
public class Dlc {

    @Id
    private String uid;

    @Column(name = "creation_date", nullable = false)
    private Instant creationDate;

    @Column(name = "game_instance_uid", nullable = false)
    private String gameInstanceUid;

    @Column(nullable = false)
    private String name;

    @Column(name = "received_date")
    private LocalDate receivedDate;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "playthrough_uid")
    private String playthroughUid;

    protected Dlc() {}

    public Dlc(
        String uid,
        String gameInstanceUid,
        String name,
        LocalDate receivedDate,
        LocalDate releaseDate,
        String playthroughUid
    ) {
        this.uid = uid;
        this.creationDate = Instant.now();
        this.gameInstanceUid = gameInstanceUid;
        this.name = name;
        this.receivedDate = receivedDate;
        this.releaseDate = releaseDate;
        this.playthroughUid = playthroughUid;
    }

    public void update(String name, LocalDate receivedDate, LocalDate releaseDate) {
        if (name != null) this.name = name;
        if (receivedDate != null) this.receivedDate = receivedDate;
        if (releaseDate != null) this.releaseDate = releaseDate;
    }

    public void setPlaythroughUid(String playthroughUid) {
        this.playthroughUid = playthroughUid;
    }

    // getters only
    public String getUid() { return uid; }
    public Instant getCreationDate() { return creationDate; }
    public String getGameInstanceUid() { return gameInstanceUid; }
    public String getName() { return name; }
    public LocalDate getReceivedDate() { return receivedDate; }
    public LocalDate getReleaseDate() { return releaseDate; }
    public String getPlaythroughUid() { return playthroughUid; }
}
