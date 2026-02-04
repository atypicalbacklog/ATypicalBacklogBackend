package at.atypicalbacklog.management.domain;

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

    @Column(name = "playthrough_uid")
    private String playthroughUid;

    protected Dlc() {}

    public Dlc(
        String uid,
        String gameInstanceUid,
        String name,
        LocalDate receivedDate,
        String playthroughUid
    ) {
        this.uid = uid;
        this.creationDate = Instant.now();
        this.gameInstanceUid = gameInstanceUid;
        this.name = name;
        this.receivedDate = receivedDate;
        this.playthroughUid = playthroughUid;
    }

    // getters only
    public String getUid() { return uid; }
    public Instant getCreationDate() { return creationDate; }
    public String getGameInstanceUid() { return gameInstanceUid; }
    public String getName() { return name; }
    public LocalDate getReceivedDate() { return receivedDate; }
    public String getPlaythroughUid() { return playthroughUid; }
}
