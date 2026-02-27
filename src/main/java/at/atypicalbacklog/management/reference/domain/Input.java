package at.atypicalbacklog.management.reference.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "\"input\"")
public class Input {

    @Id
    private String uid;

    @Column(name = "creation_date", nullable = false)
    private Instant creationDate;

    @Column(nullable = false)
    private String name;

    @Column(name = "platform_uid")
    private String platformUid;

    @Column(name = "hideinchoices", nullable = false)
    private boolean hideInChoices;

    protected Input() {}

    public String getUid() { return uid; }
    public String getName() { return name; }
    public String getPlatformUid() { return platformUid; }
    public boolean isHideInChoices() { return hideInChoices; }
}
