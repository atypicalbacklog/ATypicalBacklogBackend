package at.atypicalbacklog.management.backlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import at.atypicalbacklog.management.backlog.domain.BacklogEntry;

import java.util.List;
import java.util.Optional;

public interface BacklogRepository extends JpaRepository<BacklogEntry, String> {

    Optional<BacklogEntry> findFirstByGameUidAndRemovedDateIsNull(String gameUid);

    Optional<BacklogEntry> findFirstByGameUidAndRemovedDateIsNotNullOrderByRemovedDateDesc(String gameUid);

    List<BacklogEntry> findByGameUidOrderByAddedDateDesc(String gameUid);
}
