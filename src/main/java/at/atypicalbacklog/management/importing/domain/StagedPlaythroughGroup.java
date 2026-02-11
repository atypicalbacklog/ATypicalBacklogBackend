package at.atypicalbacklog.management.importing.domain;

import at.atypicalbacklog.management.importing.ResolutionStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "staged_playthrough_group")
public class StagedPlaythroughGroup {

    @Id
    private String uid;

    @Column(name = "import_batch_uid", nullable = false)
    private String importBatchUid;

    @Column(name = "staged_game_uid")
    private String stagedGameUid;

    @Column(name = "imported_playthrough_name")
    private String importedPlaythroughName;

    @Column(name = "inferred_type_uid")
    private String inferredTypeUid;
    @Column(name = "inferred_year")
    private Integer inferredYear;

    @Column(name = "proposed_playthrough_state_uid")
    private String proposedPlaythroughStateUid;

    @Column(name = "resolution_status")
    @Enumerated(EnumType.STRING)
    private ResolutionStatus resolutionStatus;

    public StagedPlaythroughGroup() {}

    public String getUid() { return uid; }
    public String getImportBatchUid() { return importBatchUid; }
    public String getStagedGameUid() { return stagedGameUid; }
    public String getImportedPlaythroughName() { return importedPlaythroughName; }
    public String getInferredTypeUid() { return inferredTypeUid; }
    public Integer getInferredYear() { return inferredYear; }
    public String getProposedPlaythroughStateUid() { return proposedPlaythroughStateUid; }
    public ResolutionStatus getResolutionStatus() { return resolutionStatus; }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setImportBatchUid(String importBatchUid) {
        this.importBatchUid = importBatchUid;
    }

    public void setStagedGameUid(String stagedGameUid) {
        this.stagedGameUid = stagedGameUid;
    }

    public void setImportedPlaythroughName(String importedPlaythroughName) {
        this.importedPlaythroughName = importedPlaythroughName;
    }

    public void setInferredTypeUid(String inferredTypeUid) {
        this.inferredTypeUid = inferredTypeUid;
    }

    public void setInferredYear(Integer inferredYear) {
        this.inferredYear = inferredYear;
    }

    public void setProposedPlaythroughStateUid(String proposedPlaythroughStateUid) {
        this.proposedPlaythroughStateUid = proposedPlaythroughStateUid;
    }

    public void setResolutionStatus(ResolutionStatus resolutionStatus) {
        this.resolutionStatus = resolutionStatus;
    }

    
}