package at.atypicalbacklog.management.reference.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "playthrough_state")
public class PlaythroughState {

    @Id
    private String uid;

    @Column(name = "creation_date", nullable = false)
    private Instant creationDate;

    @Column(nullable = false)
    private String name;

    protected PlaythroughState() {}

    public String getUid() { return uid; }
    public String getName() { return name; }
}
