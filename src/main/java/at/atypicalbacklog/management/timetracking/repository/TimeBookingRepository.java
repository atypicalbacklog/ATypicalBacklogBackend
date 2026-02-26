package at.atypicalbacklog.management.timetracking.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import at.atypicalbacklog.management.timetracking.domain.TimeBooking;

import java.util.Optional;
import java.util.List;

public interface TimeBookingRepository extends JpaRepository<TimeBooking, String> {

    Optional<TimeBooking> findFirstByPlaythroughUidAndEndTimeIsNull(String playthroughUid);

    List<TimeBooking> findByPlaythroughUidOrderByCreationDateAsc(String playthroughUid);

    /** Bookings that have both start and end time and have not yet been synced to Google Calendar. */
    List<TimeBooking> findByStartTimeIsNotNullAndEndTimeIsNotNullAndLastSyncDateIsNull();
}
