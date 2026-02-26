package at.atypicalbacklog.management.reference.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "tier")
public class Tier {

    @Id
    private String uid;

    @Column(name = "creation_date", nullable = false)
    private Instant creationDate;

    @Column(nullable = false)
    private String name;

    @Column(name = "\"order\"")
    private Integer order;

    protected Tier() {}

    public String getUid() { return uid; }
    public String getName() { return name; }
    public int getOrder() { return order != null ? order : 0; }
}
