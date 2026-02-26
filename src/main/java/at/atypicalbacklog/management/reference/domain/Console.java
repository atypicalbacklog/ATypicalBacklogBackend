package at.atypicalbacklog.management.reference.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "console")
public class Console {

    @Id
    private String uid;

    @Column(name = "creation_date", nullable = false)
    private Instant creationDate;

    @Column(nullable = false)
    private String name;

    private String fullname;

    protected Console() {}

    public String getUid() { return uid; }
    public String getName() { return name; }
    public String getFullname() { return fullname; }
}