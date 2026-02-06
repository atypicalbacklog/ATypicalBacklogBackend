package at.atypicalbacklog.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import at.atypicalbacklog.management.domain.GameInstance;

public interface GameInstanceRepository extends JpaRepository<GameInstance, String> {

    java.util.List<GameInstance> findByGameUid(String gameUid);
}
