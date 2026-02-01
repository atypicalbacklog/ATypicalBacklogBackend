package at.atypicalbacklog.management.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "game_completion")
public class GameCompletion {

    @Id
    private String uid;

    @Column(name = "creation_date", nullable = false)
    private Instant creationDate;

    @Column(name = "game_instance_uid", nullable = false)
    private String gameInstanceUid;

    @Column(name = "completion_state_uid", nullable = false)
    private String completionStateUid;

    @Column(name = "dlc_uid")
    private String dlcUid;

    private String info;

    protected GameCompletion() {}

    public GameCompletion(
        String uid,
        String gameInstanceUid,
        String completionStateUid,
        String dlcUid,
        String info
    ) {
        this.uid = uid;
        this.creationDate = Instant.now();
        this.gameInstanceUid = gameInstanceUid;
        this.completionStateUid = completionStateUid;
        this.dlcUid = dlcUid;
        this.info = info;
    }

    public void update(String completionStateUid, String info) {
        this.completionStateUid = completionStateUid;
        this.info = info;
    }

    // getters only
    public String getUid() {
        return uid;
    }

    public String getGameInstanceUid() {
        return gameInstanceUid;
    }

    public String getCompletionStateUid() {
        return completionStateUid;
    }

    public String getDlcUid() {
        return dlcUid;
    }

    public String getInfo() {
        return info;
    }

    

}
