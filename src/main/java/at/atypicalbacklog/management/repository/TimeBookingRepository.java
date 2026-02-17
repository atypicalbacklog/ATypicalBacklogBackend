package at.atypicalbacklog.management.repository;

import at.atypicalbacklog.management.domain.TimeBooking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface TimeBookingRepository extends JpaRepository<TimeBooking, String> {

    Optional<TimeBooking> findFirstByPlaythroughUidAndEndTimeIsNull(String playthroughUid);

    List<TimeBooking> findByPlaythroughUidOrderByCreationDateAsc(String playthroughUid);
}
