package at.atypicalbacklog.management.importing.repository;

import at.atypicalbacklog.management.importing.domain.StagedTimeBookingRaw;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StagedTimeBookingRawRepository
        extends JpaRepository<StagedTimeBookingRaw, String> {

    List<StagedTimeBookingRaw>
        findByStagedPlaythroughGroupUid(String groupUid);
}