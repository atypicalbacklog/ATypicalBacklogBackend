package at.atypicalbacklog.management.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "platform")
public class Platform {

    @Id
    private String uid;

    @Column(name = "creation_date", nullable = false)
    private Instant creationDate;

    @Column(name = "console_uid")
    private String consoleUid;

    @Column(nullable = false)
    private String name;

    @Column(name = "hideinchoices", nullable = false)
    private boolean hideInChoices;

    protected Platform() {}

    public String getUid() { return uid; }
    public String getName() { return name; }
    public String getConsoleUid() { return consoleUid; }
    public boolean isHideInChoices() { return hideInChoices; }
}
