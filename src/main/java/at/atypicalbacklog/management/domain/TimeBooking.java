package at.atypicalbacklog.management.domain;

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

    // getters
}
