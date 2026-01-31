package at.atypicalbacklog.management.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "playthrough_type")
public class PlaythroughType {

    @Id
    private String uid;

    @Column(name = "creation_date", nullable = false)
    private Instant creationDate;

    @Column(nullable = false)
    private String name;

    protected PlaythroughType() {}

    public String getUid() { return uid; }
    public String getName() { return name; }
}
