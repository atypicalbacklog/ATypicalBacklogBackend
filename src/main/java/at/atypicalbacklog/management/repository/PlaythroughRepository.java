package at.atypicalbacklog.management.repository;

import at.atypicalbacklog.management.domain.Playthrough;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaythroughRepository extends JpaRepository<Playthrough, String> {

    List<Playthrough> findByPlaythroughStateUid(String stateUid);
}
