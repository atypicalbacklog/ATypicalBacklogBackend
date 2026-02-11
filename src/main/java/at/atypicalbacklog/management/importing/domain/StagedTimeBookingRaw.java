package at.atypicalbacklog.management.importing.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "staged_time_booking_raw")
public class StagedTimeBookingRaw {

    @Id
    private String uid;

    @Column(nullable = false)
    private String importBatchUid;

    @Column(nullable = false)
    private String stagedPlaythroughGroupUid;

    @Column(nullable = false)
    private java.time.Instant startTime;

    @Column(nullable = false)
    private java.time.Instant endTime;

    public StagedTimeBookingRaw() {}

    public void setUid(String uid) {
        this.uid = uid;
    }
    public void setImportBatchUid(String importBatchUid) {
        this.importBatchUid = importBatchUid;
    }

    public void setStagedPlaythroughGroupUid(String stagedPlaythroughGroupUid) {
        this.stagedPlaythroughGroupUid = stagedPlaythroughGroupUid;
    }

    public void setStartTime(java.time.Instant startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(java.time.Instant endTime) {
        this.endTime = endTime;
    }

    
}