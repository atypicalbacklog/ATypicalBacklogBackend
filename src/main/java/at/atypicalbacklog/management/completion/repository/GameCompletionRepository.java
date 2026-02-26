package at.atypicalbacklog.management.completion.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import at.atypicalbacklog.management.completion.domain.GameCompletion;

import java.util.List;
import java.util.Optional;

public interface GameCompletionRepository
    extends JpaRepository<GameCompletion, String> {

    Optional<GameCompletion> findFirstByGameInstanceUidAndDlcUidIsNull(String gameInstanceUid);
    Optional<GameCompletion> findByDlcUid(String dlcUid);
    List<GameCompletion> findByGameInstanceUidOrderByCreationDateAsc(String gameInstanceUid);

}
