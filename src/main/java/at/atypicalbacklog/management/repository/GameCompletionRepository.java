package at.atypicalbacklog.management.repository;

import at.atypicalbacklog.management.domain.GameCompletion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GameCompletionRepository
    extends JpaRepository<GameCompletion, String> {

    Optional<GameCompletion> findFirstByGameInstanceUidAndDlcUidIsNull(String gameInstanceUid);
    Optional<GameCompletion> findByDlcUid(String dlcUid);
    List<GameCompletion> findByGameInstanceUidOrderByCreationDateAsc(String gameInstanceUid);

}
