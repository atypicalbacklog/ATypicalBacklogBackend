package at.atypicalbacklog.management.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "game_instance")
public class GameInstance {

    @Id
    private String uid;

    @Column(nullable = false)
    private Instant creationDate;

    @Column(nullable = false)
    private String gameUid;

    @Column(nullable = false)
    private String platformUid;

    @Column(nullable = false)
    private String variantUid;

    private LocalDate receivedDate;

    protected GameInstance() {}

    public GameInstance(
        String uid,
        String gameUid,
        String platformUid,
        String variantUid,
        LocalDate receivedDate
    ) {
        this.uid = uid;
        this.creationDate = Instant.now();
        this.gameUid = gameUid;
        this.platformUid = platformUid;
        this.variantUid = variantUid;
        this.receivedDate = receivedDate;
    }

    public String getUid() {
        return uid;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public String getGameUid() {
        return gameUid;
    }

    public String getPlatformUid() {
        return platformUid;
    }

    public String getVariantUid() {
        return variantUid;
    }

    public LocalDate getReceivedDate() {
        return receivedDate;
    }
}

