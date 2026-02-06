package at.atypicalbacklog.management.repository;

import at.atypicalbacklog.management.domain.BacklogEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BacklogRepository extends JpaRepository<BacklogEntry, String> {

    Optional<BacklogEntry> findFirstByGameUidAndRemovedDateIsNull(String gameUid);

    Optional<BacklogEntry> findFirstByGameUidAndRemovedDateIsNotNullOrderByRemovedDateDesc(String gameUid);

    List<BacklogEntry> findByGameUidOrderByAddedDateDesc(String gameUid);
}
