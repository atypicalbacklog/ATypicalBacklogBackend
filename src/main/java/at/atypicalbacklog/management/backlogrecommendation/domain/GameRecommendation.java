package at.atypicalbacklog.management.backlogrecommendation.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "game_recommendation")
public class GameRecommendation {

    @Id
    private String uid;

    @Column(name = "creation_date", nullable = false)
    private Instant creationDate;

    @Column(name = "name_base", nullable = false)
    private String nameBase;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "external_igdb_id", length = 100)
    private String externalIgdbId;

    @Column(name = "external_igdb_id_image_url")
    private String externalIgdbIdImageURL;

    protected GameRecommendation() {}

    public GameRecommendation(
        String uid,
        String nameBase,
        LocalDate releaseDate,
        String externalIgdbId,
        String externalIgdbIdImageURL
    ) {
        this.uid = uid;
        this.creationDate = Instant.now();
        this.nameBase = nameBase;
        this.releaseDate = releaseDate;
        this.externalIgdbId = externalIgdbId;
        this.externalIgdbIdImageURL = externalIgdbIdImageURL;
    }

    public String getUid() { return uid; }
    public Instant getCreationDate() { return creationDate; }
    public String getNameBase() { return nameBase; }
    public LocalDate getReleaseDate() { return releaseDate; }
    public String getExternalIgdbId() { return externalIgdbId; }
    public String getExternalIgdbIdImageURL() { return externalIgdbIdImageURL; }
}
