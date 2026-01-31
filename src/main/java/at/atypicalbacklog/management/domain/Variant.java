package at.atypicalbacklog.management.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "variant")
public class Variant {

    @Id
    private String uid;

    @Column(name = "creation_date", nullable = false)
    private Instant creationDate;

    @Column(nullable = false)
    private String name;

    protected Variant() {}

    public String getUid() { return uid; }
    public String getName() { return name; }
}
