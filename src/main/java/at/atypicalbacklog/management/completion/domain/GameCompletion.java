package at.atypicalbacklog.management.completion.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;

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

    @Column(name = "finished_date")
    private LocalDate finishedDate;

    @Column(name = "completed_date")
    private LocalDate completedDate;

    protected GameCompletion() {}

    public GameCompletion(
        String uid,
        String gameInstanceUid,
        String completionStateUid,
        String dlcUid,
        String info,
        LocalDate finishedDate,
        LocalDate completedDate
    ) {
        this.uid = uid;
        this.creationDate = Instant.now();
        this.gameInstanceUid = gameInstanceUid;
        this.completionStateUid = completionStateUid;
        this.dlcUid = dlcUid;
        this.info = info;
        this.finishedDate = finishedDate;
        this.completedDate = completedDate;
    }

    public void update(String completionStateUid, String info, LocalDate finishedDate, LocalDate completedDate) {
        this.completionStateUid = completionStateUid;
        this.info = info;
        if (finishedDate != null) this.finishedDate = finishedDate;
        if (completedDate != null) this.completedDate = completedDate;
    }

    public String getUid() { return uid; }
    public String getGameInstanceUid() { return gameInstanceUid; }
    public String getCompletionStateUid() { return completionStateUid; }
    public String getDlcUid() { return dlcUid; }
    public String getInfo() { return info; }
    public LocalDate getFinishedDate() { return finishedDate; }
    public LocalDate getCompletedDate() { return completedDate; }

    public void setFinishedDate(LocalDate finishedDate) { this.finishedDate = finishedDate; }
    public void setCompletedDate(LocalDate completedDate) { this.completedDate = completedDate; }
}
