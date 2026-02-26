package at.atypicalbacklog.management.timetracking.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "time_booking")
public class TimeBooking {

    @Id
    private String uid;

    @Column(nullable = false)
    private Instant creationDate;

    @Column(nullable = false)
    private String playthroughUid;

    private Instant startTime;
    private Instant endTime;

    @Column(name = "last_sync_date")
    private Instant lastSyncDate;

    protected TimeBooking() {}

    public TimeBooking(String uid, String playthroughUid, Instant startTime) {
        this.uid = uid;
        this.creationDate = Instant.now();
        this.playthroughUid = playthroughUid;
        this.startTime = startTime;
    }

    public void stop(Instant endTime) {
        this.endTime = endTime;
    }

    public boolean isOpen() {
        return endTime == null;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public String getUid() {
        return uid;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public String getPlaythroughUid() {
        return playthroughUid;
    }

    public Instant getLastSyncDate() {
        return lastSyncDate;
    }

    public void setLastSyncDate(Instant lastSyncDate) {
        this.lastSyncDate = lastSyncDate;
    }
}
