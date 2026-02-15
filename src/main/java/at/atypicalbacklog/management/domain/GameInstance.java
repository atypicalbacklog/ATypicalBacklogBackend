package at.atypicalbacklog.management.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "game_instance")
public class GameInstance {

    @Id
    private String uid;

    @Column(name = "creation_date", nullable = false)
    private Instant creationDate;

    // Optional, instance-specific display name
    private String name;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "game_uid", nullable = false)
    private String gameUid;

    @Column(name = "platform_uid", nullable = false)
    private String platformUid;

    @Column(name = "original_console_uid")
    private String originalConsoleUid;

    @Column(name = "variant_uid", nullable = false)
    private String variantUid;

    @Column(name = "received_date")
    private LocalDate receivedDate;

    @Column(name = "for_free", nullable = false)
    private boolean forFree;

    @Column(name = "in_posession", nullable = false)
    private boolean inPossession;

    @Column(name = "external_igdb_id")
    private String externalIgdbId;

    protected GameInstance() {}

    public GameInstance(
        String uid,
        Instant creationDate,
        String name,
        LocalDate releaseDate,
        String gameUid,
        String platformUid,
        String originalConsoleUid,
        String variantUid,
        LocalDate receivedDate,
        boolean forFree,
        boolean inPossession,
        String externalIgdbId
    ) {
        this.uid = uid;
        this.creationDate = creationDate;
        this.name = name;
        this.releaseDate = releaseDate;
        this.gameUid = gameUid;
        this.platformUid = platformUid;
        this.originalConsoleUid = originalConsoleUid;
        this.variantUid = variantUid;
        this.receivedDate = receivedDate;
        this.forFree = forFree;
        this.inPossession = inPossession;
        this.externalIgdbId = externalIgdbId;
    }

    // getters only
    public String getUid() { return uid; }
    public Instant getCreationDate() { return creationDate; }
    public String getName() { return name; }
    public LocalDate getReleaseDate() { return releaseDate; }
    public String getGameUid() { return gameUid; }
    public String getPlatformUid() { return platformUid; }
    public String getOriginalConsoleUid() { return originalConsoleUid; }
    public String getVariantUid() { return variantUid; }
    public LocalDate getReceivedDate() { return receivedDate; }
    public boolean isForFree() { return forFree; }
    public boolean isInPossession() { return inPossession; }
    public String getExternalIgdbId() { return externalIgdbId; }
}
