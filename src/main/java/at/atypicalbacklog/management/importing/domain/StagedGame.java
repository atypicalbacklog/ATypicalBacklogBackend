package at.atypicalbacklog.management.importing.domain;

import java.time.LocalDate;

import at.atypicalbacklog.management.importing.ResolutionStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "staged_game")
public class StagedGame {

    @Id
    private String uid;

    @Column(nullable = false)
    private String importBatchUid;

    @Column(nullable = false)
    private String proposedName;

    @Column(name = "proposed_release_date")
    private LocalDate proposedReleaseDate;

    @Column(name = "proposed_received_date")
    private LocalDate proposedReceivedDate;

    @Column(name = "proposed_variant_uid")
    private String proposedVariantUid;

    @Column(name = "proposed_platform_uid")
    private String proposedPlatformUid;

    @Column(name = "matched_game_uid")
    private String matchedGameUid;

    @Column(name = "matched_external_id")
    private String matchedExternalIgdbId;

    @Column(name = "matched_original_console_uid")
    private String matchedOriginalConsoleUid;

    @Column(name = "proposed_game_completion_state_uid")
    private String proposedCompletionStateUid;

    @Column(name = "proposed_backlog_start_date")
    private LocalDate backlogStartDate;
    @Column(name = "proposed_backlog_end_date")
    private LocalDate backlogEndDate;
    @Column(name = "proposed_backlog_reason")
    private String backlogReason;

    @Column(name = "proposed_finished_date")
    private LocalDate proposedFinishedDate;

    @Column(name = "matched_game_instance_uid")
    private String matchedGameInstanceUid;

    @Column(name = "resolution_status")
    @Enumerated(EnumType.STRING)
    private ResolutionStatus resolutionStatus;

    public StagedGame() {}

    public void updateCompletion(String completionUid) {
        this.proposedCompletionStateUid = completionUid;
    }

    public String getUid() { return uid; }
    public String getImportBatchUid() { return importBatchUid; }
    public String getProposedName() { return proposedName; }
    public LocalDate getProposedReleaseDate() { return proposedReleaseDate; }
    public LocalDate getProposedReceivedDate() { return proposedReceivedDate; }
    public String getProposedVariantUid() { return proposedVariantUid; }
    public String getProposedPlatformUid() { return proposedPlatformUid; }
    public String getMatchedGameUid() { return matchedGameUid; }
    public String getMatchedExternalIgdbId() { return matchedExternalIgdbId; }
    public String getMatchedOriginalConsoleUid() { return matchedOriginalConsoleUid; }
    public String getProposedCompletionStateUid() { return proposedCompletionStateUid; }
    public LocalDate getBacklogStartDate() { return backlogStartDate; }
    public LocalDate getBacklogEndDate() { return backlogEndDate; }
    public String getBacklogReason() { return backlogReason; }
    public LocalDate getProposedFinishedDate() { return proposedFinishedDate; }
    public String getMatchedGameInstanceUid() { return matchedGameInstanceUid; }
    public ResolutionStatus getResolutionStatus() { return resolutionStatus; }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setImportBatchUid(String importBatchUid) {
        this.importBatchUid = importBatchUid;
    }

    public void setProposedName(String proposedName) {
        this.proposedName = proposedName;
    }

    public void setProposedPlatformUid(String proposedPlatformUid) {
        this.proposedPlatformUid = proposedPlatformUid;
    }

    public void setProposedVariantUid(String proposedVariantUid) {
        this.proposedVariantUid = proposedVariantUid;
    }

    public void setProposedCompletionStateUid(String proposedCompletionStateUid) {
        this.proposedCompletionStateUid = proposedCompletionStateUid;
    }

    public void setMatchedGameInstanceUid(String matchedGameInstanceUid) {
        this.matchedGameInstanceUid = matchedGameInstanceUid;
    }

    public void setResolutionStatus(ResolutionStatus resolutionStatus) {
        this.resolutionStatus = resolutionStatus;
    }

    public void setProposedReleaseDate(LocalDate proposedReleaseDate) {
        this.proposedReleaseDate = proposedReleaseDate;
    }

    public void setProposedReceivedDate(LocalDate proposedReceivedDate) {
        this.proposedReceivedDate = proposedReceivedDate;
    }

    public void setMatchedGameUid(String matchedGameUid) {
        this.matchedGameUid = matchedGameUid;
    }

    public void setMatchedExternalIgdbId(String matchedExternalIgdbId) {
        this.matchedExternalIgdbId = matchedExternalIgdbId;
    }

    public void setMatchedOriginalConsoleUid(String matchedOriginalConsoleUid) {
        this.matchedOriginalConsoleUid = matchedOriginalConsoleUid;
    }

    public void setBacklogStartDate(LocalDate backlogStartDate) {
        this.backlogStartDate = backlogStartDate;
    }

    public void setBacklogEndDate(LocalDate backlogEndDate) {
        this.backlogEndDate = backlogEndDate;
    }

    public void setBacklogReason(String backlogReason) {
        this.backlogReason = backlogReason;
    }

    public void setProposedFinishedDate(LocalDate proposedFinishedDate) {
        this.proposedFinishedDate = proposedFinishedDate;
    }
}