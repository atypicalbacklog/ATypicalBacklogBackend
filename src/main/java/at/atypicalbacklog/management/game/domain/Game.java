package at.atypicalbacklog.management.game.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "game")
public class Game {

    @Id
    private String uid;

    @Column(nullable = false)
    private Instant creationDate;

    @Column(name = "name_base", nullable = false)
    private String name;

    private java.time.LocalDate firstReleaseDate;

    protected Game() {}

    public Game(String uid, String name, java.time.LocalDate firstReleaseDate) {
        this.uid = uid;
        this.creationDate = Instant.now();
        this.name = name;
        this.firstReleaseDate = firstReleaseDate;
    }

    public String getUid() {
        return uid;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public String getName() {
        return name;
    }

    public java.time.LocalDate getFirstReleaseDate() {
        return firstReleaseDate;
    }
}

