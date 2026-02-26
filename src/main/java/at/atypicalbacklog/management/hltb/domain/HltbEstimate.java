package at.atypicalbacklog.management.hltb.domain;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "hltb_estimate", schema = "atypicalbacklog")
public class HltbEstimate {

    @Id
    private String uid;

    @Column(name = "game_instance_uid", nullable = false)
    private String gameInstanceUid;

    @Column(name = "main_hours")
    private Float mainHours = 0f;

    @Column(name = "extra_hours")
    private Float extraHours = 0f;

    @Column(name = "complete_hours")
    private Float completeHours = 0f;

    @Column(name = "external_id", length = 64)
    private String externalId;

    @Column(name = "last_updated", nullable = false)
    private Instant lastUpdated;

    protected HltbEstimate() {}

    public HltbEstimate(
        String uid,
        String gameInstanceUid,
        Float mainHours,
        Float extraHours,
        Float completeHours,
        String externalId,
        Instant lastUpdated
    ) {
        this.uid = uid;
        this.gameInstanceUid = gameInstanceUid;
        this.mainHours = mainHours != null ? mainHours : 0f;
        this.extraHours = extraHours != null ? extraHours : 0f;
        this.completeHours = completeHours != null ? completeHours : 0f;
        this.externalId = externalId;
        this.lastUpdated = lastUpdated;
    }

    public String getUid() { return uid; }
    public String getGameInstanceUid() { return gameInstanceUid; }
    public Float getMainHours() { return mainHours; }
    public Float getExtraHours() { return extraHours; }
    public Float getCompleteHours() { return completeHours; }
    public String getExternalId() { return externalId; }
    public Instant getLastUpdated() { return lastUpdated; }

    public void setGameInstanceUid(String gameInstanceUid) { this.gameInstanceUid = gameInstanceUid; }
    public void setMainHours(Float mainHours) { this.mainHours = mainHours != null ? mainHours : 0f; }
    public void setExtraHours(Float extraHours) { this.extraHours = extraHours != null ? extraHours : 0f; }
    public void setCompleteHours(Float completeHours) { this.completeHours = completeHours != null ? completeHours : 0f; }
    public void setExternalId(String externalId) { this.externalId = externalId; }
    public void setLastUpdated(Instant lastUpdated) { this.lastUpdated = lastUpdated; }
}
