package at.atypicalbacklog.management.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "completion_state")
public class CompletionState {

    @Id
    private String uid;

    @Column(name = "creation_date", nullable = false)
    private Instant creationDate;

    @Column(nullable = false)
    private String name;

    @Column(name = "\"order\"", nullable = false)
    private int order;

    protected CompletionState() {}

    public String getUid() { return uid; }
    public String getName() { return name; }
    public int getOrder() { return order; }
}
